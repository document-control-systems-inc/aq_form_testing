package com.f2m.aquarius.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.beans.UserSession;
import com.f2m.aquarius.service.BPMService;
import com.f2m.aquarius.service.LdapService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;

@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class BPMController {
	
	private GeneralUtils gutils = new GeneralUtils();
	private LdapService ldapService = new LdapService();
	private BPMService bpmService = new BPMService();

	@RequestMapping(value = "/bpm/taskDef", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getTaskDef(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "name") String name) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.getDefTask(id, name, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}

	@RequestMapping(value = "/bpm/taskDef", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setTaskDef(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "taskDef") String taskDef) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.setDefTask(taskDef, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/taskDef", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteTaskDef(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "name") String name) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.deleteDefTask(id, name, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/task", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getTask(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String idTask,
			@RequestHeader(required = false, value = "idWorkflow") String idWorkflow,
			@RequestHeader(required = false, value = "taskDef") String taskDef,
			@RequestHeader(required = false, value = "assignedTo") String assignedTo,
			@RequestHeader(required = false, value = "status") String status) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.getTask(idTask, idWorkflow, taskDef, assignedTo, status, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}

	@RequestMapping(value = "/bpm/task", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setTask(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idTask,
			@RequestHeader(required = false, value = "taskDef") String taskDef,
			@RequestHeader(required = false, value = "idWorkflow") String idWorkflow,
			@RequestHeader(required = false, value = "assignedTo") String assignedTo,
			@RequestBody(required = false) String file) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.setTask(idTask, taskDef, idWorkflow, assignedTo, file, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/task", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteTask(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idTask) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.deleteTask(idTask, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/workflowDef", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getWorkflowDef(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String idDefWorkflow,
			@RequestHeader(required = false, value = "name") String name) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.getDefWorkflow(idDefWorkflow, name, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}

	@RequestMapping(value = "/bpm/workflowDef", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setWorkflowDef(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "defWorkflow") String defWorkflow) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.setDefWorkflow(defWorkflow, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/workflowDef", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteWorkflowDef(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDefWorkflow,
			@RequestHeader(required = false, value = "name") String workflowDefName) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.deleteDefWorkflow(idDefWorkflow, workflowDefName, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/workflow", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getWorkflow(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String idWorkflow,
			@RequestHeader(required = false, value = "idDefWorkflow") String idWorkflowDef,
			@RequestHeader(required = false, value = "status") String status) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.getWorkflow(idWorkflow, idWorkflowDef, status,userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}

	@RequestMapping(value = "/bpm/workflow", method = RequestMethod.POST, headers = "Accept=application/json")
	public ServiceResponse setWorkflow(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idWorkflow,
			@RequestHeader(required = false, value = "idDefWorkflow") String idDefWorkflow,
			@RequestBody(required = false) String file) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.setWorkflow(idWorkflow, idDefWorkflow, file, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/workflow", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteWorkflow(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idWorkflow) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.deleteWorkflow(idWorkflow, true, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/workflow/start", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse startWorkflow(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idWorkflow) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.startWorkflow(idWorkflow, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/task/list", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json;charset=UTF-8")
	public ServiceResponse getTaskList(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "status") String status) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.getTaskList(status, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/task/start", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse startTask(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.startTask(id, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/task/finish", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse finishTask(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "output") String output,
			@RequestHeader(required = false, value = "form") String form,
			@RequestHeader(required = false, value = "comments") String comments) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.finishTask(id, output, form, comments, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/task/pause", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse pauseTask(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.pauseTask(id, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/bpm/task/save", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse saveTask(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "form") String form,
			@RequestHeader(required = false, value = "comments") String comments) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(bpmService.saveTask(id, form, comments, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
}
