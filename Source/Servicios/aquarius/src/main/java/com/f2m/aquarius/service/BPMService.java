package com.f2m.aquarius.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.f2m.aquarius.parameters.BPMParameters;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BPMService {

	private GeneralUtils gutils = new GeneralUtils();
	private DBService bd = new DBService();
	private ObjectMapper mapper = new ObjectMapper();
	
	//Definición de tarea:
	public List<JsonNode> getDefTask(String idDefTask, String name, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(1401, "");
		}
		
		String where = "";
		if (idDefTask != null && idDefTask.length() > 0) {
			where += "data->>'id' = '" + idDefTask + "' ";
		}
		if (name != null && name.length() > 0) {
			if (where.length() > 0) {
				where += " and ";
			}
			where += "data->'taskDef'->>'name' = '" + name + "' ";
		}
		String orderBy = "data->'taskDef'->>'name' asc";
		List<JsonNode> defTasks = bd.selectJson("taskdef", where, orderBy);
		if (defTasks != null) {
			return defTasks;
		} else {
			throw gutils.throwException(1409, "");
		}
	}
	
	public boolean deleteDefTask(String idDefTask, String taskDefName, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(1406, "");
		}
		
		String where = "";
		boolean isOk = false;
		if (idDefTask != null && idDefTask.length() > 0) {
			where += "data->>'id' = '" + idDefTask + "' ";
			isOk = true;
		} else {
			if (taskDefName != null && taskDefName.length() > 0) {
				if (where.length() > 0) {
					where += " and ";
				}
				where += "data->'taskDef'->>'name' = '" + taskDefName + "' ";
				isOk = true;
			}
		}
		if (isOk) {
			return bd.deleteJson("taskdef", where);
		} else {
			throw gutils.throwException(1407, userId);
		}
	}
	
	public String setDefTask(String defTask, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| defTask == null || defTask.length() == 0) {
			throw gutils.throwException(1402, "");
		}
		JsonNode defTaskJson = null;
		try {
			defTaskJson = mapper.readTree(defTask);
		} catch (Exception e) {
			throw gutils.throwException(1402, "");
		}
		ObjectNode newObject = mapper.createObjectNode();
		
		String taskDefName = null;
		if (defTaskJson.findValue("name") != null) {
			taskDefName = defTaskJson.findValue("name").asText();
			List<JsonNode> taskDefExists = getDefTask(null, taskDefName, userId);
			if (taskDefExists != null && taskDefExists.size() > 0) {
				String id = taskDefExists.get(0).findValue("id").asText();
				if (deleteDefTask(id, taskDefName, userId)) {
					newObject.put("id", id);
				} else {
					newObject.put("id", UUID.randomUUID().toString());
				}
			} else {
				newObject.put("id", UUID.randomUUID().toString());
			}
			
			newObject.put("createdBy", userId);
			newObject.putPOJO("createdOn", gutils.getTime());
			newObject.putPOJO("taskDef", defTaskJson);
			if (bd.insertJson("taskdef", newObject)) {
				return newObject.findValue("id").asText();
			} else {
				throw gutils.throwException(1404, "");
			}
		} else {
			throw gutils.throwException(1402, defTask);
		}
		
	}
	
	// Tarea
	public List<JsonNode> getTask(String idTask, String workflowId, String taskDef, String assignedTo, String status, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(1405, "");
		}
		
		String where = "";
		if (idTask != null && idTask.length() > 0) {
			where += "data->>'id' = '" + idTask + "' ";
		}
		if (workflowId != null && workflowId.length() > 0) {
			if (where.length() > 0) {
				where += " and ";
			}
			where += "data->>'workflowId' = '" + workflowId + "' ";
		}
		if (taskDef != null && taskDef.length() > 0) {
			if (where.length() > 0) {
				where += " and ";
			}
			where += "data->>'taskDef' = '" + taskDef + "' ";
		}
		//TODO: assignedTo
		if (status != null && status.length() > 0) {
			if (where.length() > 0) {
				where += " and ";
			}
			if (status.contains("|")) {
				where += "(";
				String[] splitStatus = status.split("[|]+");
				for (String statusindividual:splitStatus) {
					where += "data->>'status' = '" + statusindividual + "' or ";
				}
				where = where.substring(0, where.length() - 3);
				where += ")";
			} else {
				where += "data->>'status' = '" + status + "' ";
			}
		}
		
		String orderBy = "data->'createdOn'->>'time' asc";
		List<JsonNode> defTasks = bd.selectJson("task", where, orderBy);
		if (defTasks != null) {
			return defTasks;
		} else {
			throw gutils.throwException(1408, "");
		}
	}
	
	public boolean deleteTask(String idTask, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0
				|| idTask == null || idTask.length() == 0) {
			throw gutils.throwException(1401, "");
		}
		
		String where = "";
		where += "data->>'id' = '" + idTask + "' ";
		System.out.println("Eliminando tareas...");
		return bd.deleteJson("task", where);
	}
	
	public String setTask(String idTask, String taskDef, String workflowId, String assignedTo, String file, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| taskDef == null || taskDef.length() == 0) {
			throw gutils.throwException(1402, "");
		}
		
		List<JsonNode> defTasks = getDefTask(taskDef, null, userId);
		if (defTasks == null || defTasks.isEmpty() || defTasks.size() > 1) {
			throw gutils.throwException(1410, taskDef);
		}
		boolean isMultiFile = false;
		try {
			isMultiFile = defTasks.get(0).findValue("taskDef").findValue("multifile").asBoolean(); 
		} catch (Exception e) {}
		JsonNode files = null;
		try {
			files = mapper.readTree(file);
		} catch (Exception e) {}
		if (!isMultiFile) {
			return setIndividualTask(taskDef, workflowId, assignedTo, files, defTasks.get(0), userId);
		} else {
			if (files != null) {
				if (files.findValue("type").asInt() == 0) {
					if (files.findValue("childs") != null && files.findValue("childs").isArray()) {
						String idNewTask = "";
						for (JsonNode child:files.findValue("childs")) {
							idNewTask = setIndividualTask(taskDef, workflowId, assignedTo, child, defTasks.get(0), userId); 
						}
						return idNewTask;
					} else {
						return setIndividualTask(taskDef, workflowId, assignedTo, files, defTasks.get(0), userId);
					}
				} else {
					return setIndividualTask(taskDef, workflowId, assignedTo, files, defTasks.get(0), userId);
				}
			} else {
				return setIndividualTask(taskDef, workflowId, assignedTo, files, defTasks.get(0), userId);
			}
		}
	}
	
	private String setIndividualTask(String taskDef, String workflowId, String assignedTo, JsonNode files, JsonNode defTasks, String userId) throws AquariusException {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", UUID.randomUUID().toString());
		newObject.put("taskDef", taskDef);
		newObject.put("workflowId", workflowId);
		ArrayNode assignedToNode = newObject.putArray("assignedTo");
		if (assignedTo != null) {
			String[] assignedTos = assignedTo.split("[|]+");
			for (String assign:assignedTos) {
				assignedToNode.add(assign);
			}
		} else {
			JsonNode jsonAssignedArray = defTasks.findValue("taskDef").findValue("assignedTo");
			if (jsonAssignedArray != null) {
				for (JsonNode jsonAssigned:jsonAssignedArray) {
					assignedToNode.add(jsonAssigned.asText());
				}
			}
		}
		newObject.put("createdBy", userId);
		newObject.putPOJO("createdOn", gutils.getTime());
		//TODO: Calcular
		newObject.putPOJO("dueDate", gutils.getTime());
		newObject.put("finishedOn", "");
		newObject.put("status", BPMParameters.TASK_STATUS_INITIAL);
		newObject.putPOJO("file", files);
		newObject.put("output", ""); 
		newObject.putPOJO("comments", null);
		newObject.putPOJO("form", null);
		if (bd.insertJson("task", newObject)) {
			return newObject.findValue("id").asText();
		} else {
			throw gutils.throwException(1411, "");
		}
	}
	
	public List<JsonNode> getTaskList(String status, String userId) throws AquariusException {
		List<JsonNode> response = new ArrayList<JsonNode>();
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(1429, "");
		}
		String select = "t.data as data, " +
					"d.data->'taskDef'->'name' as taskMame," + 
					"(select wf.data->'workflowDef'->'name' " +  
					"from workflow w, workflowdef wf " +
					"where w.data->>'id' = t.data->>'workflowId') as workflowName";
		String from = "task t, taskdef d";
		String where = "t.data->>'taskDef' = d.data->>'id' ";
		if (status != null && status.length() > 0) {
			if (where.length() > 0) {
				where += " and ";
			}
			if (status.contains("|")) {
				where += "(";
				String[] splitStatus = status.split("[|]+");
				for (String statusindividual:splitStatus) {
					where += "t.data->>'status' = '" + statusindividual + "' or ";
				}
				where = where.substring(0, where.length() - 3);
				where += ")";
			} else {
				where += "t.data->>'status' = '" + status + "' ";
			}
		}
		String orderBy = "t.data->'createdOn'->>'time' asc";
		List<JsonNode> lista = bd.selectColumnsJson(select, from, where, orderBy);
		for (JsonNode item:lista) {
			System.err.println(returnStringValue(item.findValue("taskmame")));
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("workflow", returnStringValue(item.findValue("workflowname")));
			newObject.put("task", returnStringValue(item.findValue("taskmame")));
			newObject.put("file", returnStringValue(item.findValue("data").findValue("file")));
			newObject.put("createdOn", gutils.getTimeString(item.findValue("data").findValue("createdOn")));
			newObject.put("dueDate", gutils.getTimeString(item.findValue("data").findValue("dueDate")));
			newObject.put("responsable", returnStringValue(item.findValue("data").findValue("assignedTo")));
			newObject.put("status", BPMParameters.getTaskStatus(item.findValue("data").findValue("status").asText()));
			newObject.put("id", returnStringValue(item.findValue("data").findValue("id")));
			response.add(newObject);
		}
		return response;
	}
	
	public boolean saveTask(String idTask, String form, String comments, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| idTask == null || idTask.length() == 0) {
			throw gutils.throwException(1430, "");
		}
		List<JsonNode> tasks = getTask(idTask, null, null, null, null, userId);
		if (!tasks.isEmpty()) {
			if (tasks.size() == 1) {
				ObjectNode task = (ObjectNode) tasks.get(0);
				return saveTask(task, form, comments, userId);
			} else {
				throw gutils.throwException(1432, idTask);
			}
		} else {
			throw gutils.throwException(1432, idTask);
		}
	}
	
	private boolean saveTask(ObjectNode task, String form, String comments, String userId) throws AquariusException {
		if (task == null) {
			throw gutils.throwException(1430, "");
		}
		String idTask = task.findValue("id").asText();
		if (idTask == null || idTask.length() == 0) {
			throw gutils.throwException(1430, "");
		}
		if (task.findValue("status").asText().equals(BPMParameters.TASK_STATUS_IN_PROGRESS)) {
			boolean modify = false;
			JsonNode jsonForm = null;
			if (form != null && form.length() > 0) {
				try {
					jsonForm = mapper.readTree(form);
					task.remove("form");
					task.putPOJO("form", jsonForm);
					modify = true;
				} catch (Exception e) {
					throw gutils.throwException(1431, "");
				}
			}
			JsonNode jsonComments = null;
			if (comments != null && comments.length() > 0) {
				try {
					jsonComments = mapper.readTree(comments);
					task.remove("comments");
					task.putPOJO("comments", jsonComments);
					modify = true;
				} catch (Exception e) {
					throw gutils.throwException(1431, "");
				}
			}
			if (modify) {
				if (deleteTask(idTask, userId)) {
					return bd.insertJson("task", task);
				} else {
					throw gutils.throwException(1433, idTask);
				}
			} else {
				return true;
			}
			
		} else {
			throw gutils.throwException(1424, idTask);
		}
	}
	

	
	public boolean startTask(String idTask, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| idTask == null || idTask.length() == 0) {
			throw gutils.throwException(1425, "");
		}
		List<JsonNode> tasks = getTask(idTask, null, null, null, BPMParameters.TASK_STATUS_INITIAL 
				+ "|" + BPMParameters.TASK_STATUS_PAUSED, userId);
		if (tasks != null && !tasks.isEmpty()) {
			ObjectNode task = (ObjectNode)tasks.get(0);
			String status = task.findValue("status").asText();
			if (deleteTask(idTask, userId)) {
				task.remove("status");
				task.put("status", BPMParameters.TASK_STATUS_IN_PROGRESS);
				if (status.equals(BPMParameters.TASK_STATUS_INITIAL)) {
					task.remove("startedOn");
					task.putPOJO("startedOn", gutils.getTime());
				}
				//TODO: De alguna forma debo de reanudar que fue pausada
				return bd.insertJson("task", task);
			} else {
				throw gutils.throwException(1423, idTask);
			}
		} else {
			throw gutils.throwException(1425, idTask);
		}
	}
	
	public boolean pauseTask(String idTask, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| idTask == null || idTask.length() == 0) {
			throw gutils.throwException(1426, "");
		}
		List<JsonNode> tasks = getTask(idTask, null, null, null, BPMParameters.TASK_STATUS_IN_PROGRESS, userId);
		if (tasks != null && !tasks.isEmpty()) {
			ObjectNode task = (ObjectNode)tasks.get(0);
			if (deleteTask(idTask, userId)) {
				task.remove("status");
				task.put("status", BPMParameters.TASK_STATUS_PAUSED);
				//TODO: De alguna forma le debo indicar que fue pausada...
				return bd.insertJson("task", task);
			} else {
				throw gutils.throwException(1423, idTask);
			}
		} else {
			throw gutils.throwException(1425, idTask);
		}
	}
	
	public String finishTask(String idTask, String output, String form, String comments, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| idTask == null || idTask.length() == 0
				|| output == null || output.length() == 0) {
			throw gutils.throwException(1422, "");
		}
		String response = "";
		List<JsonNode> tasks = getTask(idTask, null, null, null, BPMParameters.TASK_STATUS_IN_PROGRESS, userId);
		if (tasks != null && !tasks.isEmpty()) {
			ObjectNode task = (ObjectNode)tasks.get(0);
			String idWorkflow = task.findValue("workflowId").asText();
			String taskDef = task.findValue("taskDef").asText();
			if (idWorkflow != null && idWorkflow.length() > 0) {
				List<JsonNode> workflows = getWorkflow(idWorkflow, null, null, userId);
				if (workflows != null && !workflows.isEmpty()) {
					ObjectNode workflow = (ObjectNode)workflows.get(0);
					String idDefWorkflow = workflow.findValue("workflowDef").asText();
					List<JsonNode> workflowDefs = getDefWorkflow(idDefWorkflow, null, userId);
					if (workflowDefs != null && !workflowDefs.isEmpty()) {
						JsonNode workflowDef = workflowDefs.get(0);
						JsonNode wfTasks = workflowDef.findValue("workflowDef").findValue("tasks");
						JsonNode files = workflow.findValue("file");
						String strFiles = "";
						if (files != null) {
							strFiles = files.toString();
						}
						for (JsonNode wfTask:wfTasks) {
							if (wfTask.findValue("taskDef").asText().equals(taskDef)) {
								for (JsonNode option: wfTask.findValue("options")) {
									if (option.findValue("option").asText().equals(output)) {
										String newTask = option.findValue("nextTask").asText();
										if (newTask != null && newTask.length() > 0) {
											response = setTask(null, option.findValue("nextTask").asText(), idWorkflow, null, strFiles, userId);
										} else {
											finishWorkflow(idWorkflow, userId);
										}
									}
								}
							}
						}
					}
				}
			}
			if (saveTask(task, form, comments, userId)) {
				if (deleteTask(idTask, userId)) {
					task.remove("output");
					task.put("output", output);
					task.remove("status");
					task.put("status", BPMParameters.TASK_STATUS_FINALIZED);
					task.remove("finishedOn");
					task.putPOJO("finishedOn", gutils.getTime());
					if (bd.insertJson("task", task)) {
						return response;
					} else {
						throw gutils.throwException(1423, idTask);
					}
				} else {
					throw gutils.throwException(1423, idTask);
				}
			} else {
				throw gutils.throwException(1423, idTask);
			}
		} else {
			throw gutils.throwException(1422, idTask);
		}
	}
	
	//Definición de Workflow 
	public List<JsonNode> getDefWorkflow(String idDefWorkflow, String name, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(1412, "");
		}
		
		String where = "";
		if (idDefWorkflow != null && idDefWorkflow.length() > 0) {
			where += "data->>'id' = '" + idDefWorkflow + "' ";
		}
		if (name != null && name.length() > 0) {
			if (where.length() > 0) {
				where += " and ";
			}
			where += "data->>'name' = '" + name + "' ";
		}
		String orderBy = "data->>'name' asc";
		List<JsonNode> defTasks = bd.selectJson("workflowdef", where, orderBy);
		if (defTasks != null) {
			return defTasks;
		} else {
			throw gutils.throwException(1413, "");
		}
	}
	
	public boolean deleteDefWorkflow(String idDefWorkflow, String workflowDefName, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(1414, "");
		}
		
		String where = "";
		boolean isOk = false;
		if (idDefWorkflow != null && idDefWorkflow.length() > 0) {
			where += "data->>'id' = '" + idDefWorkflow + "' ";
			isOk = true;
		} else {
			if (workflowDefName != null && workflowDefName.length() > 0) {
				if (where.length() > 0) {
					where += " and ";
				}
				where += "data->>'name' = '" + workflowDefName + "' ";
				isOk = true;
			}
		}
		if (isOk) {
			return bd.deleteJson("workflowdef", where);
		} else {
			throw gutils.throwException(1415, userId);
		}
	}
	
	public String setDefWorkflow(String defWorkflow, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| defWorkflow == null || defWorkflow.length() == 0) {
			throw gutils.throwException(1402, "");
		}
		JsonNode defWorkflowJson = null;
		try {
			defWorkflowJson = mapper.readTree(defWorkflow);
		} catch (Exception e) {
			throw gutils.throwException(1402, "");
		}
		ObjectNode newObject = mapper.createObjectNode();
		
		String workflowDefName = null;
		if (defWorkflowJson.findValue("name") != null) {
			workflowDefName = defWorkflowJson.findValue("name").asText();
			List<JsonNode> workflowDefExists = getDefWorkflow(null, workflowDefName, userId);
			if (workflowDefExists != null && workflowDefExists.size() > 0) {
				String id = workflowDefExists.get(0).findValue("id").asText();
				if (deleteDefWorkflow(id, workflowDefName, userId)) {
					newObject.put("id", id);
				} else {
					newObject.put("id", UUID.randomUUID().toString());
				}
			} else {
				newObject.put("id", UUID.randomUUID().toString());
			}
			
			newObject.put("createdBy", userId);
			newObject.putPOJO("createdOn", gutils.getTime());
			newObject.putPOJO("workflowDef", defWorkflowJson);
			if (bd.insertJson("workflowdef", newObject)) {
				return newObject.findValue("id").asText();
			} else {
				throw gutils.throwException(1416, "");
			}
		} else {
			throw gutils.throwException(1402, defWorkflow);
		}
		
	}
	
	private String returnStringValue(JsonNode node) {
		String response = null;
		if (!node.isNull()) {
			response = node.asText();
		}
		return response;
	}
	
	// Wotkflow:	
	public List<JsonNode> getWorkflow(String idWorkflow, String idWorkflowDef, String status, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0) {
			throw gutils.throwException(1417, "");
		}
		
		String where = "";
		if (idWorkflow != null && idWorkflow.length() > 0) {
			where += "data->>'id' = '" + idWorkflow + "' ";
		}
		if (idWorkflowDef != null && idWorkflowDef.length() > 0) {
			if (where.length() > 0) {
				where += " and ";
			}
			where += "data->>'workflowDef' = '" + idWorkflowDef + "' ";
		}
		if (status != null && status.length() > 0) {
			if (where.length() > 0) {
				where += " and ";
			}
			if (status.contains("|")) {
				where += "(";
				String[] splitStatus = status.split("[|]+");
				for (String statusindividual:splitStatus) {
					where += "data->>'status' = '" + statusindividual + "' or ";
				}
				where = where.substring(0, where.length() - 3);
				where += ")";
			} else {
				where += "data->>'status' = '" + status + "' ";
			}
		}
		
		String orderBy = "data->'createdOn'->>'time' asc";
		List<JsonNode> defTasks = bd.selectJson("workflow", where, orderBy);
		if (defTasks != null) {
			return defTasks;
		} else {
			throw gutils.throwException(1413, "");
		}
	}
	
	public boolean deleteWorkflow(String idWorkflow, boolean deleteTask, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0
				|| idWorkflow == null || idWorkflow.length() == 0) {
			throw gutils.throwException(1414, "");
		}
		if(deleteTask) {
			List<JsonNode> tareas = getTask(null, idWorkflow, null, null, null, userId);
			for (JsonNode tarea:tareas) {
				String id = tarea.findValue("id").asText();
				deleteTask(id, userId);
			}
		}
		String where = "data->>'id' = '" + idWorkflow + "' ";
		return bd.deleteJson("workflow", where);
	}
	
	public String setWorkflow(String idWorkflow, String idDefWorkflow, String file, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| idDefWorkflow == null || idDefWorkflow.length() == 0) {
			throw gutils.throwException(1402, "");
		}
		List<JsonNode> workflowDefs = getDefWorkflow(idDefWorkflow, null, userId);
		if (workflowDefs != null && !workflowDefs.isEmpty()) {
			ObjectNode newObject = mapper.createObjectNode();
			if (idWorkflow != null && idWorkflow.length() > 0) {
				List<JsonNode> workflowInstances = getWorkflow(idWorkflow, null, null, userId);
				if (workflowInstances != null && !workflowInstances.isEmpty()) {
					deleteWorkflow(idWorkflow, true, userId);
					newObject.put("id", idWorkflow);
				} else {
					newObject.put("id", UUID.randomUUID().toString());
				}
			} else {
				newObject.put("id", UUID.randomUUID().toString());
			}
			newObject.put("workflowDef", idDefWorkflow);
			newObject.put("createdBy", userId);
			newObject.putPOJO("createdOn", gutils.getTime());
			newObject.put("status", BPMParameters.WF_STATUS_INITIAL);
			if (file != null && file.length() > 0) {
				try {
					JsonNode files = mapper.readTree(file);
					if (files.findValue("name") != null) {
						newObject.putPOJO("file", files);
					}
				} catch (Exception e) { }
			}
			if (bd.insertJson("workflow", newObject)) {
				return newObject.findValue("id").asText();
			} else {
				throw gutils.throwException(1416, "");
			}
		} else {
			throw gutils.throwException(1402, idDefWorkflow);
		}
	}
	
	public String startWorkflow(String idWorkflow, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| idWorkflow == null || idWorkflow.length() == 0) {
			throw gutils.throwException(1420, "");
		}
		String response = "";
		List<JsonNode> workflows = getWorkflow(idWorkflow, null, BPMParameters.WF_STATUS_INITIAL, userId);
		if (workflows != null && !workflows.isEmpty()) {
			ObjectNode workflow = (ObjectNode)workflows.get(0);
			String idDefWorkflow = workflow.findValue("workflowDef").asText();
			List<JsonNode> workflowDefs = getDefWorkflow(idDefWorkflow, null, userId);
			if (workflowDefs != null && !workflowDefs.isEmpty()) {
				JsonNode workflowDef = workflowDefs.get(0);
				JsonNode tasks = workflowDef.findValue("workflowDef").findValue("tasks");
				JsonNode files = workflow.findValue("file");
				String strFiles = "";
				if (files != null) {
					strFiles = files.toString();
				}
				for (JsonNode task:tasks) {
					if (task.findValue("order").asInt() == 0) {
						response = setTask(null, task.findValue("taskDef").asText(), idWorkflow, null, strFiles, userId);
					}
				}
				if (response.length() > 0) {
					if (deleteWorkflow(idWorkflow, false, userId)) {
						workflow.remove("status");
						workflow.put("status", BPMParameters.WF_STATUS_IN_PROGRESS);
						workflow.remove("startedOn");
						workflow.putPOJO("startedOn", gutils.getTime());
						if (bd.insertJson("workflow", workflow)) {
							return response;
						} else {
							throw gutils.throwException(1421, idWorkflow);
						}
					} else {
						throw gutils.throwException(1421, idWorkflow);
					}
				} else {
					return response;
				}
			} else {
				throw gutils.throwException(1420, idDefWorkflow);
			}
		} else {
			throw gutils.throwException(1420, idWorkflow);
		}
	}
	
	public boolean finishWorkflow(String idWorkflow, String userId) throws AquariusException {
		if (userId == null || userId.length() == 0 
				|| idWorkflow == null || idWorkflow.length() == 0) {
			throw gutils.throwException(1427, "");
		}
		List<JsonNode> workflows = getWorkflow(idWorkflow, null, BPMParameters.WF_STATUS_IN_PROGRESS, userId);
		if (workflows != null && !workflows.isEmpty()) {
			ObjectNode workflow = (ObjectNode)workflows.get(0);
			if (deleteWorkflow(idWorkflow, false, userId)) {
				workflow.remove("status");
				workflow.put("status", BPMParameters.WF_STATUS_FINALIZED);
				workflow.remove("finishedOn");
				workflow.putPOJO("finishedOn", gutils.getTime());
				return bd.insertJson("workflow", workflow);
			} else {
				throw gutils.throwException(1421, idWorkflow);
			}
		} else {
			throw gutils.throwException(1427, idWorkflow);
		}
	}
	
	public static void main(String[] args) {
		BPMService bpm = new BPMService();
		try {
			List<JsonNode> renglones = bpm.getTaskList(null, "DEMO");
			for (JsonNode renglon:renglones) {
				System.out.println(renglon);
			}
			/*
			List<JsonNode> tasks = bpm.getTask(null, null, null, null, BPMParameters.TASK_STATUS_IN_PROGRESS + "|" + BPMParameters.TASK_STATUS_FINALIZED, "DEMO");
			if (tasks != null) {
				for (JsonNode task:tasks) {
					String idTask = task.findValue("id").asText();
					System.out.println("Task: " + idTask);
					String taskDef = task.findValue("taskDef").asText();
					String option = "";
					switch(taskDef) {
						case "0e2c80a2-b7df-4325-a75b-8358d981dd12":
							option = "Recibido";
							break;
						case "2a60dcbc-cf9e-4ed2-9846-6781cb42038b":
							option = "Registrado";
							break;
						case "35394e85-f398-4016-847b-37e7a581a3d7":
							option = "Análisis Completado";
							break;
						case "495a5322-44cb-436b-b7aa-b54544fffa9f":
							option = "Recibido";
							break;
						case "61376e01-ef96-4f71-be30-bab9064dda4c":
							option = "Revisado";
							break;
					}
					System.out.println("Iniciando Tarea: " + bpm.startTask(idTask, "DEMO"));
					System.out.println("Finalizando Tarea: " + bpm.finishTask(idTask, option, "DEMO"));
				}
			}
			*/
		} catch (AquariusException e) {
			e.printStackTrace();
		}
	}
}
