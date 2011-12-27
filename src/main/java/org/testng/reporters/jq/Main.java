package org.testng.reporters.jq;

import static org.testng.reporters.jq.BasePanel.C;
import static org.testng.reporters.jq.BasePanel.D;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.internal.Utils;
import org.testng.reporters.Files;
import org.testng.reporters.XMLStringBuffer;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main implements IReporter {
  private static final String[] RESOURCES = new String[] {
    "jquery-1.7.1.min.js", "testng-reports.css", "testng-reports.js",
    "passed.png", "failed.png", "skipped.png", "navigator-bullet.png",
    "bullet_point.png"
  };

  private Model m_model;
  private String m_outputDirectory;

  @Override
  public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
      String outputDirectory) {
    m_model = new Model(suites);
    m_outputDirectory = outputDirectory + File.separatorChar + "new";

    XMLStringBuffer xsb = new XMLStringBuffer("  ");

    // Top banner
    new BannerPanel(m_model).generate(xsb);

    // Navigator on the left hand side
    TestNgXmlPanel testNgPanel = new TestNgXmlPanel(m_model);
    TestPanel testPanel = new TestPanel(m_model);
    GroupPanel groupPanel = new GroupPanel(m_model);
    TimesPanel timePanel = new TimesPanel(m_model);
    new NavigatorPanel(m_model, testNgPanel, testPanel, groupPanel, timePanel).generate(xsb);

    xsb.push(D, C, "wrapper");
    xsb.push(D, "class", "main-panel-root");

    //
    // Suite panels
    //
    new SuitePanel(m_model).generate(xsb);

    //
    // Group panel
    groupPanel.generate(xsb);

    //
    // Times panel
    //
    timePanel.generate(xsb);

    //
    // Panel that displays the list of test names
    //
    testPanel.generate(xsb);

    //
    // Panel that displays the content of testng.xml
    //
    testNgPanel.generate(xsb);

    xsb.pop(D); // main-panel-root
    xsb.pop(D); // wrapper

    String all;
    try {
      InputStream head3 = getClass().getResourceAsStream("/head3");
      if (head3 == null) {
        throw new RuntimeException("Couldn't find resource head3");
      } else {
        for (String fileName : RESOURCES) {
          InputStream is = getClass().getResourceAsStream("/" + fileName);
          if (is == null) {
            throw new AssertionError("Couldn't find resource: " + fileName);
          }
          Files.copyFile(is, new File(m_outputDirectory, fileName));
        }
        all = Files.readFile(head3);
        Utils.writeFile(m_outputDirectory, "index.html", all + xsb.toXML());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
