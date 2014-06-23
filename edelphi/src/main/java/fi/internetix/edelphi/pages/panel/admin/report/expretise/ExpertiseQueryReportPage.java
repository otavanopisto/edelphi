package fi.internetix.edelphi.pages.panel.admin.report.expretise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.birt.chart.model.Chart;

import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.edelphi.utils.ReportUtils;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.controllers.RequestContext;

public class ExpertiseQueryReportPage extends QueryReportPageController {

  public ExpertiseQueryReportPage() {
    super(QueryPageType.EXPERTISE);
  }

  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    

    Query query = queryPage.getQuerySection().getQuery();
    Panel panel = ResourceUtils.getResourcePanel(query);
    if (panel != null) {
      List<String> xTickLabels = new ArrayList<String>();
      List<String> yTickLabels = new ArrayList<String>();
      
      List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);
      Collections.sort(expertiseClasses, new Comparator<PanelUserExpertiseClass>() {
        @Override
        public int compare(PanelUserExpertiseClass o1, PanelUserExpertiseClass o2) {
          return o1.getId().compareTo(o2.getId());
        }
      });

      List<PanelUserIntressClass> intrestClasses = panelUserIntressClassDAO.listByPanel(panel);
      Collections.sort(intrestClasses, new Comparator<PanelUserIntressClass>() {
        @Override
        public int compare(PanelUserIntressClass o1, PanelUserIntressClass o2) {
          return o1.getId().compareTo(o2.getId());
        }
      });
      
      Map<Long, Integer> expertiseIndexMap = new HashMap<Long, Integer>();
      Map<Long, Integer> intrestIndexMap = new HashMap<Long, Integer>();
      
      int expretiseClassCount = expertiseClasses.size();
      int interestClassCount = intrestClasses.size();
      
      for (int i = 0; i < expretiseClassCount; i++) {
        PanelUserExpertiseClass expertiseClass = expertiseClasses.get(i);
        expertiseIndexMap.put(expertiseClass.getId(), i);
        xTickLabels.add(expertiseClass.getName());
      }
      
      for (int i = 0; i < interestClassCount; i++) {
        PanelUserIntressClass intressClass = intrestClasses.get(i);
        intrestIndexMap.put(intressClass.getId(), (interestClassCount - 1) - i);
        yTickLabels.add(0, intressClass.getName());
      }

      int maxX = expretiseClassCount + 1;
      int maxY = interestClassCount + 1;
          
      Double[][] values = new Double[maxX][];
      for (int x = 0; x < maxX; x++) {
        values[x] = new Double[maxY];
      }
      
      List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());

      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName(expertiseClass));
        for (QueryReply queryReply : queryReplies) {
          QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
          
          if (answer != null) {
            for (QueryOptionFieldOption option : answer.getOptions()) {
              Long interestId = NumberUtils.createLong(option.getValue());

              int indexX = expertiseIndexMap.get(expertiseClass.getId());
              int indexY = intrestIndexMap.get(interestId);

              values[indexX][indexY] = new Double(values[indexX][indexY] != null ? values[indexX][indexY] + 1 : 1); 
            }
          }
        }
      }      

      return ChartModelProvider.createBubbleChart(queryPage.getTitle(), null, xTickLabels, null, yTickLabels, 90, 0, values);
    } else {
      // TODO: Proper exception
      throw new RuntimeException("Could not find query panel");
    }
  }

  private String getFieldName(PanelUserExpertiseClass expertiseClass) {
    return "expertise." + expertiseClass.getId();
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    QueryUtils.appendQueryPageComments(requestContext, queryPage);
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/expertise.jsp", null);
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    // TODO comments
//    QueryUtils.appendQueryPageComments(requestContext, queryPage);
    QueryReportPage reportPage = new QueryReportPage(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/todo.jsp");
    return reportPage;
  }

}