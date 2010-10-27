<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<head>
	  <title><fmt:message key="system.hibernateStatistics.pageTitle"/></title>
	  <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
	  <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
	  <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
	  
	  <script type="text/javascript">
	    
	    function onLoad(event) {
	      var tabControl = new IxProtoTabs($('tabs'));
	      var cacheTabsControl = new IxProtoTabs($('cacheTabs'));
	    }
	
	  </script>
    
  </head>
  <body onload="onLoad(event)">
	  <jsp:include page="/templates/generic/header.jsp"></jsp:include>
	  
	  <h1 class="genericPageHeader"><fmt:message key="system.hibernateStatistics.pageTitle" /></h1>
	  
	  <div id="hibernateStatisticContainer"> 
      <div class="genericFormContainer"> 
        <div class="tabLabelsContainer" id="tabs">
          <a class="tabLabel" href="#hibernate">
            <fmt:message key="system.hibernateStatistics.tabLabelHibernate"/>
          </a>
          <span class="containsNestedTabs"><a class="tabLabel" href="#ehcache">
            <fmt:message key="system.hibernateStatistics.tabLabelEHCache"/>
          </a></span>
        </div>
        
        <div id="hibernate" class="tabContent">
			    <table>
			      <tr>
			        <td>sessions opened</td>
			        <td>${statistics.sessionOpenCount}</td>
			      </tr>
			      <tr>
			        <td>sessions closed</td>
			        <td>${statistics.sessionCloseCount}</td>
			      </tr>
			      <tr>
			        <td>transactions</td>
			        <td>${statistics.transactionCount}</td>
			      </tr>
			      <tr>
			        <td>successful transactions</td>
			        <td>${statistics.successfulTransactionCount}</td>
			      </tr>
			      <tr>
			        <td>optimistic lock failures</td>
			        <td>${statistics.optimisticFailureCount}</td>
			      </tr>
			      <tr>
			        <td>flushes</td>
			        <td>${statistics.flushCount}</td>
			      </tr>
			      <tr>
			        <td>connections obtained</td>
			        <td>${statistics.connectCount}</td>
			      </tr>
			      <tr>
			        <td>statements prepared</td>
			        <td>${statistics.prepareStatementCount}</td>
			      </tr>
			      <tr>
			        <td>statements closed</td>
			        <td>${statistics.closeStatementCount}</td>
			      </tr>
			      <tr>
			        <td>second level cache puts</td>
			        <td>${statistics.secondLevelCachePutCount}</td>
			      </tr>
			      <tr>
			        <td>second level cache hits</td>
			        <td>${statistics.secondLevelCacheHitCount}</td>
			      </tr>
			      <tr>
			        <td>second level cache misses</td>
			        <td>${statistics.secondLevelCacheMissCount}</td>
			      </tr>
			      <tr>
			        <td>entities loaded</td>
			        <td>${statistics.entityLoadCount}</td>
			      </tr>
			      <tr>
			        <td>entities updated</td>
			        <td>${statistics.entityUpdateCount}</td>
			      </tr>
			      <tr>
			        <td>entities inserted</td>
			        <td>${statistics.entityInsertCount}</td>
			      </tr>
			      <tr>
			        <td>entities deleted</td>
			        <td>${statistics.entityDeleteCount}</td>
			      </tr>
			      <tr>
			        <td>entities fetched</td>
			        <td>${statistics.entityFetchCount}</td>
			      </tr>
			      <tr>
			        <td>collections loaded</td>
			        <td>${statistics.collectionLoadCount}</td>
			      </tr>
			      <tr>
			        <td>collections updated</td>
			        <td>${statistics.collectionUpdateCount}</td>
			      </tr>
			      <tr>
			        <td>collections removed</td>
			        <td>${statistics.collectionRemoveCount}</td>
			      </tr>
			      <tr>
			        <td>collections recreated</td>
			        <td>${statistics.collectionRecreateCount}</td>
			      </tr>
			      <tr>
			        <td>collections fetched</td>
			        <td>${statistics.collectionFetchCount}</td>
			      </tr>
			      <tr>
			        <td>queries executed to database</td>
			        <td>${statistics.queryExecutionCount}</td>
			      </tr>
			      <tr>
			        <td>query cache puts</td>
			        <td>${statistics.queryCachePutCount}</td>
			      </tr>
			      <tr>
			        <td>query cache hits</td>
			        <td>${statistics.queryCacheHitCount}</td>
			      </tr>
			      <tr>
			        <td>query cache misses</td>
			        <td>${statistics.queryCacheMissCount}</td>
			      </tr>
			      <tr>
			        <td>max query time</td>
			        <td>${statistics.queryExecutionMaxTime}</td>
			      </tr>
			    </table>
				</div>
				  
			  <div id="ehcache" class="tabContent tabContentNestedTabs">
			    <div class="tabLabelsContainer" id="cacheTabs">
			      <c:forEach var="cache" items="${cacheInfos}">
			      <a class="tabLabel" href="#ehcache.${cache.name}">
               ${cache.nameShort}
	          </a>
			      </c:forEach>
			    </div>
    
			    <c:forEach var="cache" items="${cacheInfos}">
						<div id="ehcache.${cache.name}" class="tabContentContainer">
							<div class="tabContentLeftTopCorner"></div>
		          <div class="tabContentRightTopCorner"></div>
		          <div class="tabContentRightBottomCorner"></div>
		          <div class="tabContentLeftBottomCorner"></div>
		          <div class="tabContent">
								<table>
									<tr>
										<td>Elements in cache</td>
										<td>${cache.elementsInCache}</td>
									</tr>
									<tr>
										<td>Elements in memory cache</td>
										<td>${cache.elementsInMemory}</td>
									</tr>
									<tr>
										<td>Elements in disk cache</td>
										<td>${cache.elementsInDisk}</td>
									</tr>
									<tr>
										<td>Hits</td>
										<td>${cache.hits}</td>
									</tr>
									<tr>
										<td>Memory hits</td>
										<td>${cache.memoryHits}</td>
									</tr>
									<tr>
										<td>Disk hits</td>
										<td>${cache.diskHits}</td>
									</tr>
									<tr>
										<td>Cache misses</td>
										<td>${cache.misses}</td>
									</tr>
									<tr>
										<td>Evicted elements</td>
										<td>${cache.evictionCount}</td>
									</tr>
								</table>
							</div>
						</div>
					</c:forEach>
	      </div>
		  </div>	    
		</div> 
	  
	  <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>