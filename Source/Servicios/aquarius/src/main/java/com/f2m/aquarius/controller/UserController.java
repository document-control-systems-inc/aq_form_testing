package com.f2m.aquarius.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.f2m.aquarius.beans.Group;
import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.beans.User;
import com.f2m.aquarius.beans.UserSession;
import com.f2m.aquarius.service.LdapService;
import com.f2m.aquarius.service.MailService;
import com.f2m.aquarius.utils.GeneralUtils;
import com.f2m.aquarius.utils.RandomString;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class UserController {
	
	private LdapService ldapService = new LdapService();
	private MailService mailService = new MailService();
	private ObjectMapper mapper = new ObjectMapper();
	private GeneralUtils gutils = new GeneralUtils();
	private RandomString randomPassword = new RandomString(10);
	
	// Login/Logout
	
	@RequestMapping(value = "/login", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse login(@RequestHeader(required = false, value = "user") String user, 
			@RequestHeader(required = false, value = "password") String password) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			User tmpUser = ldapService.getUserByDomain(user);
			if (tmpUser != null) {
				tmpUser.setPassword(password);
				if (ldapService.checkPassword(tmpUser)) {
					/*
					JsonNode userJson = null;
					try {
						userJson = userService.getUserByCn(user);
					} catch (AquariusException e) {
						int errorCode = gutils.getErrorCode(e.getMessage());
						if (errorCode == 121) {
							userJson = userService.setUser(user, tmpUser);
						} else {
							response.setStatus(errorCode);
							return response;
						}
					}
					*/
					UserSession session = new UserSession();
					session.setUserUid(user);
					session.setHostnameConnected("localhost");
					if (!ldapService.validateSession(session)) {
						session = ldapService.createSession(session);
						if (session != null && session.getSessionId() != null) {
							response.setExito(session.getSessionId().toString());
							if (response.getExito() != null && response.getExito().toString().length() > 0) {
								response.setStatus(0);
							} else {
								response.setStatus(9);
							}
						} else {
							response.setStatus(6);
						}
					} else {
						response.setStatus(2);
					}
				} else {
					response.setStatus(3);
				}
			} else {
				response.setStatus(101);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse logout(@RequestHeader(required = false, value = "token") String token) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.deleteSession(session)) {
				response.setStatus(0);
			} else {
				response.setStatus(8);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/logoutAll", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse logoutAll(@RequestHeader(required = false, value = "user") String user) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setUserUid(user);
			session.setHostnameConnected("localhost");
			if (ldapService.deleteAllSessionsOfUser(session)) {
				response.setStatus(0);
			} else {
				response.setStatus(8);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/restorePassword", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse restorePassword(@RequestHeader(required = false, value = "user") String user, 
			@RequestHeader(required = false, value = "mail") String mail) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			User tmpUser = ldapService.getUserByDomain(user);
			if (tmpUser != null) {
				if (tmpUser.getMail().equals(mail)) {
					String newPassword = randomPassword.nextString();
					System.out.println("Se ha generado el nuevo Password: " + newPassword);
					tmpUser.setPassword(newPassword);
					if (ldapService.insertUser(tmpUser)) {
						String message = "<p>Estimado " + tmpUser.getGivenName() + " " + tmpUser.getLastName() + ":</p>" +
								"<p> Usted ha solicitado una nueva contraseña.</p><p>La nueva contraseña generada es: <b>" +
								newPassword + "</b></p>";
						if (mailService.sendMail(tmpUser.getMail(), "Generación de Nueva Contraseña", message)) {
							response.setStatus(0);
						} else {
							response.setStatus(602);
						}
					} else {
						response.setStatus(107);
					}
				} else {
					response.setStatus(101);
				}
			} else {
				response.setStatus(101);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	// Usuarios:
	@RequestMapping(value = "/users", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse getUsers(@RequestHeader(required = false, value = "token") String token) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(ldapService.listAllUsers());
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/user/dn", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse getUserByDN(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "dn") String dn) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(ldapService.getUserByDN(dn));
				if (response.getExito() != null) {
					response.setStatus(0);
				} else {
					response.setStatus(101);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/user/domain", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse getUserByDomain(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "domain") String domain) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(ldapService.getUserByDomain(domain));
				if (response.getExito() != null) {
					response.setStatus(0);
				} else {
					response.setStatus(101);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/user/uid", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse getUserByUid(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "uid") String uid) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(ldapService.getUserByUid(uid));
				if (response.getExito() != null) {
					response.setStatus(0);
				} else {
					response.setStatus(101);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/user/uid", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteUserByUid(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "uid") String uid) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				List<User> usuarios = ldapService.getUserByUid(uid);
				if (usuarios == null || usuarios.size() == 0) {
					response.setStatus(101);
				} else {
					if (usuarios.size() > 1) {
						response.setStatus(102);	
					} else {
						if (ldapService.deleteUser(usuarios.get(0).getDn())) {
							response.setStatus(0);
						} else {
							response.setStatus(118);
						}
					}
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/user/domain", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteUserByDomain(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "domain") String domain) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				User user = ldapService.getUserByDomain(domain);
				if (user == null || user.getDn() == null || user.getDn().length() == 0) {
					response.setStatus(101);	
				} else {
					if (ldapService.deleteUser(user.getDn())) {
						response.setStatus(0);
					} else {
						response.setStatus(118);
					}
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/user/dn", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteUserByDN(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "dn") String dn) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				if (ldapService.deleteUser(dn)) {
					response.setStatus(0);
				} else {
					response.setStatus(118);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/user", method = RequestMethod.PUT,headers="Accept=application/json")
	public ServiceResponse insertUser(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "user") String user) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			User newUser = mapper.readValue(user, User.class);
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				if (ldapService.insertUser(newUser)) {
					response.setStatus(0);
				} else {
					response.setStatus(119);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	
	
	// Grupos:
	@RequestMapping(value = "/groups", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse getGroups(@RequestHeader(required = false, value = "token") String token) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(ldapService.listAllGroups());
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/group/dn", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse getGroupByDN(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "dn") String dn) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(ldapService.getGroupByDN(dn));
				if (response.getExito() != null) {
					response.setStatus(0);
				} else {
					response.setStatus(201);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/group/domain", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse getGroupByDomain(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "domain") String domain) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(ldapService.getGroupByDomain(domain));
				if (response.getExito() != null) {
					response.setStatus(0);
				} else {
					response.setStatus(201);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/group/cn", method = RequestMethod.GET,headers="Accept=application/json")
	public ServiceResponse getGroupByCN(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "cn") String cn) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(ldapService.getGroupByCN(cn));
				if (response.getExito() != null) {
					response.setStatus(0);
				} else {
					response.setStatus(201);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/group/cn", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteGroupByCN(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "cn") String cn) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				List<Group> grupos = ldapService.getGroupByCN(cn);
				if (grupos == null || grupos.size() == 0) {
					response.setStatus(201);
				} else {
					if (grupos.size() > 1) {
						response.setStatus(202);	
					} else {
						if (ldapService.deleteGroup(grupos.get(0).getDn())) {
							response.setStatus(0);
						} else {
							response.setStatus(215);
						}
					}
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/group/domain", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteGroupByDomain(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "domain") String domain) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				Group group = ldapService.getGroupByDomain(domain);
				if (group == null || group.getDn() == null || group.getDn().length() == 0) {
					response.setStatus(201);	
				} else {
					if (ldapService.deleteGroup(group.getDn())) {
						response.setStatus(0);
					} else {
						response.setStatus(215);
					}
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/group/dn", method = RequestMethod.DELETE,headers="Accept=application/json")
	public ServiceResponse deleteGroupByDN(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "dn") String dn) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				if (ldapService.deleteGroup(dn)) {
					response.setStatus(0);
				} else {
					response.setStatus(215);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/group", method = RequestMethod.PUT,headers="Accept=application/json")
	public ServiceResponse insertGroup(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "group") String group) {
		ServiceResponse response = new ServiceResponse();
		response.setStatus(-1);
		try {
			Group newGroup = mapper.readValue(group, Group.class);
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				if (ldapService.insertGroup(newGroup)) {
					response.setStatus(0);
				} else {
					response.setStatus(216);
				}
			} else {
				response.setStatus(6);
			}
		} catch (Exception e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	
	public static void main(String[] args) {
		UserController controller = new UserController();
		//controller.restorePassword("ecm", "adrian.martinez@f2m.com.mx");
		ServiceResponse response = controller.login("ecm", "aquarius01");
		String token = response.getExito().toString();
		response = controller.getUserByUid(token, "ecm");
		System.out.println(response);
		
		
	
	}
}
