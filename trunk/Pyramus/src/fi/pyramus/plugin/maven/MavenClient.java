package fi.pyramus.plugin.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.wagon.Wagon;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.impl.ArtifactDescriptorReader;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.impl.MetadataResolver;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.impl.RepositoryEventDispatcher;
import org.sonatype.aether.impl.SyncContextFactory;
import org.sonatype.aether.impl.UpdateCheckManager;
import org.sonatype.aether.impl.VersionRangeResolver;
import org.sonatype.aether.impl.VersionResolver;
import org.sonatype.aether.impl.internal.DefaultArtifactResolver;
import org.sonatype.aether.impl.internal.DefaultFileProcessor;
import org.sonatype.aether.impl.internal.DefaultLocalRepositoryProvider;
import org.sonatype.aether.impl.internal.DefaultMetadataResolver;
import org.sonatype.aether.impl.internal.DefaultRemoteRepositoryManager;
import org.sonatype.aether.impl.internal.DefaultRepositoryEventDispatcher;
import org.sonatype.aether.impl.internal.DefaultRepositorySystem;
import org.sonatype.aether.impl.internal.DefaultSyncContextFactory;
import org.sonatype.aether.impl.internal.DefaultUpdateCheckManager;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManagerFactory;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResolutionException;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.spi.io.FileProcessor;
import org.sonatype.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.version.Version;

public class MavenClient {

  public MavenClient(File localRepositoryDirectory) {
    RepositoryEventDispatcher repositoryEventDispatcher = new DefaultRepositoryEventDispatcher();
    SyncContextFactory syncContextFactory = new DefaultSyncContextFactory();

    UpdateCheckManager updateCheckManager = new DefaultUpdateCheckManager();
    RemoteRepositoryManager remoteRepositoryManager = createRepositoryManager(updateCheckManager);
    MetadataResolver metadataResolver = createMetadataResolver(remoteRepositoryManager, repositoryEventDispatcher, syncContextFactory, updateCheckManager);
    VersionResolver versionResolver = createVersionResolver(repositoryEventDispatcher, metadataResolver);

    this.localRepositoryPath = localRepositoryDirectory.getAbsolutePath();
    this.artifactResolver = createArtifactResolver(repositoryEventDispatcher, syncContextFactory, remoteRepositoryManager, versionResolver);
    this.artifactDescriptorReader = createArtifactDescriptionReader(repositoryEventDispatcher, versionResolver, artifactResolver, remoteRepositoryManager);
    this.versionRangeResolver = createVersionRangeResolver(metadataResolver, repositoryEventDispatcher, syncContextFactory);
    this.systemSession = createSystemSession();
  }

  public List<Version> listVersions(String groupId, String artifactId) throws VersionRangeResolutionException {
    Artifact artifact = new DefaultArtifact(groupId, artifactId, "jar", "[0.0.0,999.999.999]");
    VersionRangeRequest request = new VersionRangeRequest(artifact, repositories, null);
    VersionRangeResult rangeResult = versionRangeResolver.resolveVersionRange(systemSession, request);
    return rangeResult.getVersions();
  }

  public ArtifactDescriptorResult describeArtifact(String groupId, String artifactId, String version) throws ArtifactResolutionException, ArtifactDescriptorException {
    ArtifactResult artifactResult = artifactResolver.resolveArtifact(systemSession, new ArtifactRequest(new DefaultArtifact(groupId, artifactId, "jar", version), repositories, null));
    ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(artifactResult.getArtifact(), repositories, null);
    ArtifactDescriptorResult descriptorResult = artifactDescriptorReader.readArtifactDescriptor(systemSession, request);
    
    for (Dependency dependency : descriptorResult.getDependencies()) {
      if ("compile".equals(dependency.getScope())) {
        artifactResolver.resolveArtifact(systemSession, new ArtifactRequest(dependency.getArtifact(), repositories, null));
      }
    }
    
    return descriptorResult;
  }

  public File getArtifactJarFile(Artifact artifact) {
    String pathForLocalArtifact = systemSession.getLocalRepositoryManager().getPathForLocalArtifact(artifact);
    return new File(systemSession.getLocalRepository().getBasedir(), pathForLocalArtifact);
  }
  
  public void addRepository(String url) {
    repositories.add(new RemoteRepository(null, "default", url));
  }
  
  public void removeRepository(String url) {
    for (RemoteRepository remoteRepository : repositories) {
      if (remoteRepository.getUrl().equals(url)) {
        repositories.remove(remoteRepository);
        return;
      }
    }
  }

  private ArtifactDescriptorReader createArtifactDescriptionReader(RepositoryEventDispatcher repositoryEventDispatcher, VersionResolver versionResolver,
      ArtifactResolver defaultArtifactResolver, RemoteRepositoryManager remoteRepositoryManager) {
    DefaultModelBuilderFactory modelBuilderFactory = new DefaultModelBuilderFactory();
    DefaultModelBuilder modelBuilder = modelBuilderFactory.newInstance();
    
    DefaultArtifactDescriptorReader artifactDescriptorReader = new DefaultArtifactDescriptorReader();
    artifactDescriptorReader.setVersionResolver(versionResolver);
    artifactDescriptorReader.setArtifactResolver(defaultArtifactResolver);
    artifactDescriptorReader.setModelBuilder(modelBuilder);
    artifactDescriptorReader.setRepositoryEventDispatcher(repositoryEventDispatcher);
    artifactDescriptorReader.setRemoteRepositoryManager(remoteRepositoryManager);
    
    return artifactDescriptorReader;
  }

  private ArtifactResolver createArtifactResolver(RepositoryEventDispatcher repositoryEventDispatcher, SyncContextFactory syncContextFactory,
      RemoteRepositoryManager remoteRepositoryManager, VersionResolver versionResolver) {
    DefaultArtifactResolver artifactResolver = new DefaultArtifactResolver();
    artifactResolver.setSyncContextFactory(syncContextFactory);
    artifactResolver.setRepositoryEventDispatcher(repositoryEventDispatcher);
    artifactResolver.setVersionResolver(versionResolver);
    artifactResolver.setRemoteRepositoryManager(remoteRepositoryManager);
    return artifactResolver;
  }

  private VersionResolver createVersionResolver(RepositoryEventDispatcher repositoryEventDispatcher, MetadataResolver metadataResolver) {
    DefaultVersionResolver versionResolver = new DefaultVersionResolver();
    versionResolver.setRepositoryEventDispatcher(repositoryEventDispatcher);
    versionResolver.setMetadataResolver(metadataResolver);
    return versionResolver;
  }

  private MetadataResolver createMetadataResolver(RemoteRepositoryManager remoteRepositoryManager, RepositoryEventDispatcher repositoryEventDispatcher,
      SyncContextFactory syncContextFactory, UpdateCheckManager updateCheckManager) {
    DefaultMetadataResolver metadataResolver = new DefaultMetadataResolver();
    metadataResolver.setSyncContextFactory(syncContextFactory);
    metadataResolver.setRepositoryEventDispatcher(repositoryEventDispatcher);
    metadataResolver.setRemoteRepositoryManager(remoteRepositoryManager);
    metadataResolver.setUpdateCheckManager(updateCheckManager);
    return metadataResolver;
  }

  private VersionRangeResolver createVersionRangeResolver(MetadataResolver metadataResolver, RepositoryEventDispatcher repositoryEventDispatcher,
      SyncContextFactory syncContextFactory) {
    DefaultVersionRangeResolver versionRangeResolver = new DefaultVersionRangeResolver();
    versionRangeResolver.setMetadataResolver(metadataResolver);
    versionRangeResolver.setRepositoryEventDispatcher(repositoryEventDispatcher);
    versionRangeResolver.setSyncContextFactory(syncContextFactory);
    return versionRangeResolver;
  }

  private RemoteRepositoryManager createRepositoryManager(UpdateCheckManager updateCheckManager) {
    DefaultRemoteRepositoryManager remoteRepositoryManager = new DefaultRemoteRepositoryManager();
//    FileRepositoryConnectorFactory fileRepositoryConnector = new FileRepositoryConnectorFactory();
//    remoteRepositoryManager.addRepositoryConnectorFactory(fileRepositoryConnector);
    FileProcessor fileProcessor = new DefaultFileProcessor();
    WagonRepositoryConnectorFactory wagonRepositoryConnectorFactory = new WagonRepositoryConnectorFactory();
    wagonRepositoryConnectorFactory.setWagonProvider(new WagonProvider() {
      @Override
      public void release(Wagon wagon) {
      }

      @Override
      public Wagon lookup(String roleHint) throws Exception {
        return new HttpWagon();
      }
    });

    wagonRepositoryConnectorFactory.setFileProcessor(fileProcessor);
    remoteRepositoryManager.addRepositoryConnectorFactory(wagonRepositoryConnectorFactory);
    remoteRepositoryManager.setUpdateCheckManager(updateCheckManager);

    return remoteRepositoryManager;
  }

  private RepositorySystemSession createSystemSession() {
    LocalRepositoryManagerFactory factory = new SimpleLocalRepositoryManagerFactory();
    DefaultLocalRepositoryProvider repositoryProvider = new DefaultLocalRepositoryProvider();
    repositoryProvider.addLocalRepositoryManagerFactory(factory);

    DefaultRepositorySystem repositorySystem = new DefaultRepositorySystem();
    repositorySystem.setLocalRepositoryProvider(repositoryProvider);

    MavenRepositorySystemSession session = new MavenRepositorySystemSession();
    LocalRepository localRepo = new LocalRepository(localRepositoryPath);
    session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(localRepo));
    
    return session;
  }

  private List<RemoteRepository> repositories = new ArrayList<RemoteRepository>();
  private String localRepositoryPath;
  private ArtifactResolver artifactResolver;
  private ArtifactDescriptorReader artifactDescriptorReader;
  private VersionRangeResolver versionRangeResolver;
  private RepositorySystemSession systemSession;
}
