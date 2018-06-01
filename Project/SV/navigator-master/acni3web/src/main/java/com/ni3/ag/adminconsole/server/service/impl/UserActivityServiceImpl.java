/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.server.dao.UserActivityDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.UserActivityService;
import com.ni3.ag.adminconsole.shared.service.def.UserLanguageService;
import com.ni3.ag.adminconsole.util.TimeUtil;
import com.ni3.ag.adminconsole.validation.ACException;

public class UserActivityServiceImpl implements UserActivityService{
	private static final Logger log = Logger.getLogger(UserActivityServiceImpl.class);
	private static final String SESSION_ID_PARAM = "SessionId";

	private UserActivityDAO userActivityDAO;
	private UserDAO userDAO;
	private UserLanguageService userLanguageService;
	private String logoImage;
	private String reportTemplate;

	public void setUserActivityDAO(UserActivityDAO userActivityDAO){
		this.userActivityDAO = userActivityDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public void setUserLanguageService(UserLanguageService userLanguageService){
		this.userLanguageService = userLanguageService;
	}

	public void setLogoImage(String logoImage){
		this.logoImage = logoImage;
	}

	public void setReportTemplate(String reportTemplate){
		this.reportTemplate = reportTemplate;
	}

	@Override
	public List<User> getUsersWithActivities(Date from, Date to, User user){
		log.debug("Requesting activities for user(s)");
		log.debug("User: " + (user != null ? user.getUserName() : "all users"));
		log.debug("Period: from " + from + " to " + to);
		List<User> users = userActivityDAO.getUsersWithActivities(from, to, user);

		for (User u : users){
			List<UserActivity> parsedActivities = new ArrayList<UserActivity>();
			for (UserActivity activity : u.getActivities()){
				UserActivityType type = activity.getUserActivityType();
				if (type != null && type != UserActivityType.NotALog)
					parsedActivities.add(activity);
			}
			u.setActivities(parsedActivities);
		}

		for (User u : users){
			fillSessionDurations(u.getActivities());
		}
		return users;
	}

	void fillSessionDurations(List<UserActivity> activities){
		Map<String, UserActivity> loginActivityMap = new HashMap<String, UserActivity>();
		for (UserActivity activity : activities){
			String session = getSessionString(activity.getRequest());
			if (session == null)
				continue;

			if (activity.isLoginActivity() || activity.isSyncLoginActivity()){
				loginActivityMap.put(session, activity);
			} else{
				UserActivity loginActivity = loginActivityMap.get(session);
				if (loginActivity == null)
					continue;
				long duration = TimeUtil.getDurationInMillis(loginActivity.getDateTime(), activity.getDateTime());
				loginActivity.setSessionDuration(duration);

				if (activity.isLogoutActivity()){
					loginActivityMap.remove(session);
				}
			}
		}
	}

	private String getSessionString(String request){
		int from = request.indexOf(SESSION_ID_PARAM);
		int to = request.indexOf(";", from);
		if (from >= 0 && to >= 0)
			return request.substring(from, to);
		return null;
	}

	@Override
	public Map<UserActivityType, List<UserActivity>> getActionsWithUsers(Date from, Date to, UserActivityType filter){
		log.debug("Requesting activities for action");
		log.debug("User: " + (filter != null ? filter.getValue() : "all actions"));
		log.debug("Period: from " + from + " to " + to);
		List<UserActivity> actions = userActivityDAO.getActionsWithUsers(from, to, filter);
		Map<UserActivityType, List<UserActivity>> result = new HashMap<UserActivityType, List<UserActivity>>();
		if (filter == null)
			for (UserActivityType uat : UserActivityType.values())
				result.put(uat, new ArrayList<UserActivity>());
		else
			result.put(filter, new ArrayList<UserActivity>());

		for (UserActivity ua : actions){
			UserActivityType type = ua.getUserActivityType();

			if (type == null || type == UserActivityType.NotALog || (filter != null && !filter.equals(type)))
				continue;

			result.get(type).add(ua);
		}

		fillSessionDurations(actions);
		return result;
	}

	@Override
	public List<User> getUsers(){
		List<User> users = userDAO.getUsers();
		Collections.sort(users, new Comparator<User>(){
			@Override
			public int compare(User o1, User o2){
				return o1.getUserName().compareTo(o2.getUserName());
			}
		});
		return users;
	}

	@Override
	public String getCurrentServerTime(String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}

	@Override
	public byte[] getXLSReport(Date from, Date to, Object mode, Object filter, Language language) throws ACException{
		JasperPrint jasperPrint = (JasperPrint) getReport(from, to, mode, filter, language);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			JExcelApiExporter exp = new JExcelApiExporter();
			exp.setParameter(JRExporterParameter.OUTPUT_STREAM, bos);
			exp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exp.setParameter(JExcelApiExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
			exp.setParameter(JExcelApiExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exp.exportReport();
		} catch (JRException e){
			log.error(e);
			throw new ACException(TextID.MsgXLSExportFailed);
		}
		return bos.toByteArray();
	}

	@Override
	public byte[] getPDFReport(Date from, Date to, Object mode, Object filter, Language language) throws ACException{
		JasperPrint jasperPrint = (JasperPrint) getReport(from, to, mode, filter, language);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			JasperExportManager.exportReportToPdfStream(jasperPrint, bos);
		} catch (JRException e){
			log.error(e);
			throw new ACException(TextID.MsgPDFExportFailed);
		}
		return bos.toByteArray();
	}

	@Override
	public byte[] getHTMLReport(Date from, Date to, Object mode, Object filter, Language language) throws ACException{
		JasperPrint jasperPrint = (JasperPrint) getReport(from, to, mode, filter, language);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try{
			Ni3JRHtmlExporter exp = new Ni3JRHtmlExporter();
			exp.setParameter(JRExporterParameter.OUTPUT_STREAM, bos);
			exp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exp.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exp.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
			exp.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
			exp.exportReport();

		} catch (JRException e){
			log.error(e);
			throw new ACException(TextID.MsgHTMLExportFailed);
		}
		return bos.toByteArray();
	}

	public Object getReport(Date from, Date to, Object mode, Object filter, Language language) throws ACException{
		JasperPrint jasperPrint = null;
		try{
			List<Map<String, Object>> data = null;
			Map<String, Object> parameters = null;
			if (mode == null || TextID.UserBased.equals(mode)){
				List<User> users = getUsersWithActivities(from, to, (User) filter);
				data = prepareDataForReport(users, language);
				long avgTime = getAvgDuration(users);
				Map<TextID, Integer> activityCounts = getActivityCounts(users);
				parameters = prepareReportParameters(from, to, true, filter, language, avgTime, activityCounts);
			} else if (TextID.ActionBased.equals(mode)){
				Map<UserActivityType, List<UserActivity>> actions = getActionsWithUsers(from, to, (UserActivityType) filter);
				data = prepareDataForReport(actions, language);
				long avgTime = getAvgDuration(actions);
				Map<TextID, Integer> activityCounts = getActivityCounts(actions);
				parameters = prepareReportParameters(from, to, false, filter, language, avgTime, activityCounts);
			}
			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(data);

			InputStream is = UserActivityServiceImpl.class.getResourceAsStream(reportTemplate);

			JasperReport jasperReport = JasperCompileManager.compileReport(is);

			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, ds);

		} catch (JRException e){
			log.error(e);
			throw new ACException(TextID.MsgInvalidTemplateForReport);
		}
		return jasperPrint;
	}

	private Map<String, Object> prepareReportParameters(Date from, Date to, boolean userMode, Object filter,
			Language language, long avgTime, Map<TextID, Integer> activityCounts){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("titletrl", userLanguageService.getLabelById(TextID.Monitoring, language));
		parameters.put("fromtrl", userLanguageService.getLabelById(TextID.DateFrom, language));
		parameters.put("totrl", userLanguageService.getLabelById(TextID.DateTo, language));
		parameters.put("datefrom", from);
		parameters.put("dateto", to);
		parameters.put("modetrl", userLanguageService.getLabelById(TextID.FilterMode, language));
		parameters.put("mode", userLanguageService.getLabelById(userMode ? TextID.UserBased : TextID.ActionBased, language));
		if (filter != null){
			parameters.put("useractiontrl", userLanguageService.getLabelById(userMode ? TextID.User : TextID.Action,
					language));
			if (userMode)
				parameters.put("useraction", ((User) filter).getUserName());
			else{
				TextID value = ((UserActivityType) filter).getValue();
				parameters.put("useraction", userLanguageService.getLabelById(value, language));
			}
		}
		parameters.put("usertrl", userLanguageService.getLabelById(userMode ? TextID.User : TextID.Action, language));
		parameters.put("actiontrl", userLanguageService.getLabelById(userMode ? TextID.Action : TextID.User, language));
		parameters.put("timestamptrl", userLanguageService.getLabelById(TextID.DateTime, language));
		parameters.put("logoImage", loadImage(logoImage));

		parameters.put("summarytrl", userLanguageService.getLabelById(TextID.Summary, language));
		parameters.put("avgtimetrl", userLanguageService.getLabelById(TextID.AvgSessionDuration, language) + ": ");
		parameters.put("avgtime", TimeUtil.getFormattedTime(avgTime));
		parameters.put("actiontotaltrl", userLanguageService.getLabelById(TextID.TotalsByAction, language));
		parameters.put("actioncounttrl", getActionTotalNames(activityCounts, language));
		parameters.put("actioncount", getActionTotalValues(activityCounts, language));
		return parameters;
	}

	private String getActionTotalNames(Map<TextID, Integer> activityCounts, Language language){
		String result = "";
		int index = 0;
		for (TextID ac : activityCounts.keySet()){
			result += userLanguageService.getLabelById(ac, language) + ":";
			if (activityCounts.size() - 1 > index++)
				result += "\n";
		}
		return result;
	}

	private String getActionTotalValues(Map<TextID, Integer> activityCounts, Language language){
		String result = "";
		int index = 0;
		for (TextID ac : activityCounts.keySet()){
			result += activityCounts.get(ac);
			if (activityCounts.size() - 1 > index++)
				result += "\n";
		}
		return result;
	}

	private List<Map<String, Object>> prepareDataForReport(List<User> users, Language language){
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (User user : users){
			for (UserActivity activity : user.getActivities()){
				Map<String, Object> row = new LinkedHashMap<String, Object>();
				row.put("username", user.getUserName());
				row.put("action", userLanguageService.getLabelById(activity.getUserActivityType().getValue(), language));
				row.put("timestamp", activity.getDateTime());
				if ((activity.isLoginActivity() || activity.isSyncLoginActivity()) && activity.getSessionDuration() != null)
					row.put("duration", TimeUtil.getFormattedTime(activity.getSessionDuration()));
				else if (activity.isSync())
					row.put("duration", userLanguageService.getLabelById(TextID.Sync, language));
				data.add(row);
			}
		}

		return data;
	}

	private List<Map<String, Object>> prepareDataForReport(Map<UserActivityType, List<UserActivity>> activities,
			Language language){
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (UserActivityType activityType : UserActivityType.values()){
			if (activityType.equals(UserActivityType.NotALog) || activities.get(activityType) == null)
				continue;
			for (UserActivity activity : activities.get(activityType)){
				Map<String, Object> row = new LinkedHashMap<String, Object>();
				// username and action columns are switched
				row.put("username", userLanguageService.getLabelById(activity.getUserActivityType().getValue(), language));
				row.put("action", activity.getUser().getUserName());
				row.put("timestamp", activity.getDateTime());
				if ((activity.isLoginActivity() || activity.isSyncLoginActivity()) && activity.getSessionDuration() != null)
					row.put("duration", TimeUtil.getFormattedTime(activity.getSessionDuration()));
				else if (activity.isSync())
					row.put("duration", userLanguageService.getLabelById(TextID.Sync, language));
				data.add(row);
			}
		}

		return data;
	}

	@Override
	public Object[] getDataWithSummary(Date from, Date to, Object mode, Object filter){
		Object[] data = new Object[3];
		if (TextID.UserBased.equals(mode)){
			List<User> users = getUsersWithActivities(from, to, (User) filter);
			data[0] = users;
			data[1] = getAvgDuration(users);
			data[2] = getActivityCounts(users);
		} else if (TextID.ActionBased.equals(mode)){
			Map<UserActivityType, List<UserActivity>> actions = getActionsWithUsers(from, to, (UserActivityType) filter);
			data[0] = actions;
			data[1] = getAvgDuration(actions);
			data[2] = getActivityCounts(actions);
		}
		return data;
	}

	Map<TextID, Integer> getActivityCounts(Map<UserActivityType, List<UserActivity>> activities){
		Map<TextID, Integer> countMap = new LinkedHashMap<TextID, Integer>();
		for (UserActivityType activityType : UserActivityType.values()){
			if (activityType.equals(UserActivityType.NotALog))
				continue;

			List<UserActivity> list = activities.get(activityType);
			int count = 0;
			if (list != null)
				count = list.size();

			if (activityType.equals(UserActivityType.PasswordLogin) || activityType.equals(UserActivityType.SIDLogin)){
				Integer loginCount = countMap.get(TextID.Login);
				if (loginCount == null)
					loginCount = 0;
				countMap.put(TextID.Login, loginCount + count);
			} else
				countMap.put(activityType.getValue(), count);

			log.debug("Activity: " + activityType + ", count: " + count);
		}

		return countMap;
	}

	Map<TextID, Integer> getActivityCounts(List<User> users){
		Map<TextID, Integer> countMap = new LinkedHashMap<TextID, Integer>();
		for (UserActivityType activityType : UserActivityType.values()){
			if (activityType.equals(UserActivityType.NotALog))
				continue;

			int count = 0;
			for (User user : users){
				count += getActivityCount(user.getActivities(), activityType);
			}

			if (activityType.equals(UserActivityType.PasswordLogin) || activityType.equals(UserActivityType.SIDLogin)){
				Integer loginCount = countMap.get(TextID.Login);
				if (loginCount == null)
					loginCount = 0;
				countMap.put(TextID.Login, loginCount + count);
			} else
				countMap.put(activityType.getValue(), count);

			log.debug("Activity: " + activityType + ", count: " + count);
		}

		return countMap;
	}

	long getAvgDuration(Map<UserActivityType, List<UserActivity>> activities){
		List<UserActivity> passLoginActivities = activities.get(UserActivityType.PasswordLogin);
		List<UserActivity> sidLoginActivities = activities.get(UserActivityType.SIDLogin);
		if ((passLoginActivities == null || passLoginActivities.isEmpty())
				&& (sidLoginActivities == null || sidLoginActivities.isEmpty()))
			return 0;
		long total = getTotalDuration(passLoginActivities);
		total += getTotalDuration(sidLoginActivities);
		int count = (passLoginActivities == null ? 0 : passLoginActivities.size())
				+ (sidLoginActivities == null ? 0 : sidLoginActivities.size());
		long avg = total / count;
		log.debug("Average session duration: " + avg);
		return avg;
	}

	long getAvgDuration(List<User> users){
		long total = 0;
		int totalLogins = 0;
		for (User user : users){
			List<UserActivity> activities = user.getActivities();
			total += getTotalDuration(activities);
			totalLogins += getTotalLoginCount(activities);
		}
		if (totalLogins == 0)
			return 0;
		long avg = total / (totalLogins);
		log.debug("Average session duration: " + avg);
		return avg;
	}

	long getTotalDuration(List<UserActivity> activities){
		if (activities == null)
			return 0;
		long total = 0;
		for (UserActivity activity : activities){
			if (activity.isLoginActivity() && activity.getSessionDuration() != null && activity.getSessionDuration() > 0){
				total += activity.getSessionDuration();
			}
		}
		return total;
	}

	int getTotalLoginCount(List<UserActivity> activities){
		if (activities == null)
			return 0;
		int loginCount = 0;
		for (UserActivity activity : activities){
			if (activity.isLoginActivity()){
				loginCount++;
			}
		}
		return loginCount;
	}

	int getActivityCount(List<UserActivity> activities, UserActivityType activityType){
		int count = 0;
		for (UserActivity activity : activities){
			if (activityType.getValue().toString().equals(activity.getActivityType())){
				count++;
			}
		}
		return count;
	}

	public static Image loadImage(String imageName){
		try{
			InputStream is = UserActivityServiceImpl.class.getResourceAsStream(imageName);
			Image image = ImageIO.read(is);

			return image;
		} catch (Exception e){
			log.error("Can not load icon: " + imageName);
			return null;
		}
	}

}
