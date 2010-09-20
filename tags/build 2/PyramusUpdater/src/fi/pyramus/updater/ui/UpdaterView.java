package fi.pyramus.updater.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;


public class UpdaterView extends VisualView {
  
  private static final long serialVersionUID = 1L;
  
  public UpdaterView(UpdaterViewController updaterController) {
    super("Pyramus updater", 800, 600);
    
    this.updaterController = updaterController;
    buildUi();
    attachListeners();
  }
  
  private void attachListeners() {
    this.upgradeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updaterController.performUpgrade();
      }
    });
  }
  
  private void buildUi() {
    // center panel

    JPanel topPanel = new JPanel(new GridLayout(2, 0));
    
    updatesList = new JList();
    updatesList.setEnabled(false);
    JScrollPane updatesListScrollPane = new JScrollPane(updatesList);
    
    JPanel updatesListPanel = createLabeledComponent("Required updates", updatesListScrollPane);
    updatesListPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
    topPanel.add(updatesListPanel);
    
    logTextArea = new JTextArea();
    JScrollPane logTextAreaScrollPane = new JScrollPane(logTextArea);
    JPanel logPanel = createLabeledComponent("Log", logTextAreaScrollPane);
    logPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
    logTextAreaScrollPane.setAutoscrolls(true);
    topPanel.add(logPanel);  

    Logger.getRootLogger().addAppender(new JTextAreaAppender(logTextArea));
    
    // bottom panel
    
    JPanel bottomPanel = new JPanel(new BorderLayout());
    
    upgradeButton = new JButton("Start upgrade");
    
    bottomPanel.add(upgradeButton, BorderLayout.CENTER);
    
    getContentPane().add(topPanel, BorderLayout.CENTER);
    getContentPane().add(bottomPanel, BorderLayout.SOUTH);
  }
  
  public void setUpdateListItems(String[] items) {
    updatesList.setListData(items);  
  }
  
  private UpdaterViewController updaterController;
  private JButton upgradeButton;
  private JList updatesList; 
  private JTextArea logTextArea;
  
  private class JTextAreaAppender extends WriterAppender {
    
    public JTextAreaAppender(JTextArea jTextArea) {
      super(new PatternLayout(), new StringWriter());
      
      this.logTextArea = jTextArea;
    }
    
    @Override
    public void append(LoggingEvent event) {
      logTextArea.append(layout.format(event));
    }
    
    private JTextArea logTextArea;
  }
}
