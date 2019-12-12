package com.f2m.aquarius.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.beans.UserSession;
import com.f2m.aquarius.service.CmisService;
import com.f2m.aquarius.service.FileService;
import com.f2m.aquarius.service.LdapService;
import com.f2m.aquarius.utils.AquariusException;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@CrossOrigin(methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class FileController {
	
	private GeneralUtils gutils = new GeneralUtils();
	private LdapService ldapService = new LdapService();
	private CmisService cmis = new CmisService();
	private FileService fileService = new FileService();
	
	@RequestMapping(value = "/tree/root", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getRootTreeFolder(@RequestHeader(required = false, value = "token") String token) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getTree(userUid));
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
	
	@RequestMapping(value = "/tree/multiselect/root", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getRootTreeMultiselectFolder(@RequestHeader(required = false, value = "token") String token) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getMultiSelectTree(userUid));
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

	@RequestMapping(value = "/domain", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getDomains(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDomain) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getDomains(idDomain, userUid));
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
	
	@RequestMapping(value = "/domain", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setDomain(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "domain") String domainName,
			@RequestHeader(required = false, value = "id") String idDomain) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setDomain(domainName, idDomain, userUid));
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
	
	@RequestMapping(value = "/root", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getRootFolder(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "domain") String idDomain,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getRoot(idDomain, type, userUid));
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
	
	@RequestMapping(value = "/root/name", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getRootFolderbyName(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "domain") String idDomain,
			@RequestHeader(required = false, value = "name") String name) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getRootByName(idDomain, name, userUid));
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
	
	@RequestMapping(value = "/root/name", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setRootFolderbyName(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "domain") String idDomain,
			@RequestHeader(required = false, value = "name") String name) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setFolder(name, "", idDomain, true, userUid));
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
	
	@RequestMapping(value = "/childrens/id", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getFoldersById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idParent,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getChildren(idParent, type, userUid));
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
	
	@RequestMapping(value = "/childrens/path", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getFoldersByPath(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "path") String pathParent,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getChildren(cmis.getObjectByPath(pathParent, userUid).findValue("id").asText(), type, userUid));
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
	
	@RequestMapping(value = "/object/id", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getObjectById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getObjectById(id, userUid));
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
	
	@RequestMapping(value = "/object/id", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse updateObjectById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "name") String name,
			@RequestHeader(required = false, value = "visible") boolean visible) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					if (name != null && name.length() > 0) {
						response.setExito(cmis.updateNodeName(id, name, userUid));
						response.setStatus(0);
					} else {
						response.setExito(cmis.updateNodeVisibility(id, visible, userUid));
						response.setStatus(0);
					}
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
	
	@RequestMapping(value = "/object/path", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getObjectByPath(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "path") String path) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getObjectByPath(path, userUid));
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
	
	@RequestMapping(value = "/object/info", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getObjectInfo(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "idMetadata") String idMetadata,
			@RequestHeader(required = false, value = "numVersionMetadata") String numVersionMetadata,
			@RequestHeader(required = false, value = "idContent") String idContent,
			@RequestHeader(required = false, value = "numVersionContent") String numVersionContent,
			@RequestHeader(required = false, value = "lastVersion") boolean lastVersion) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					int versionMetadata = -1;
					if (numVersionMetadata != null) {
						try {
							versionMetadata = Integer.parseInt(numVersionMetadata);
						} catch (Exception e) {}
					}
					int versionContent = -1;
					if (numVersionContent != null) {
						try {
							versionContent = Integer.parseInt(numVersionContent);
						} catch (Exception e) {}
					}
					response.setExito(cmis.getObjectProperties(id, idMetadata, versionMetadata, idContent, versionContent, lastVersion, userUid));
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
	
	@RequestMapping(value = "/folder/id", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setFolderbyName(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "domain") String idDomain,
			@RequestHeader(required = false, value = "name") String name, 
			@RequestHeader(required = false, value = "id") String idParent) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setFolder(name, idParent, idDomain, true, userUid));
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
	
	@RequestMapping(value = "/object/move", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse moveObjectbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idFolder,
			@RequestHeader(required = false, value = "idParent") String idParent) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					//TODO:
					response.setExito(cmis.moveFolder(idFolder, idParent, userUid));
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
	
	@RequestMapping(value = "/folder/move", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse moveFolderbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idFolder,
			@RequestHeader(required = false, value = "idParent") String idParent) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.moveFolder(idFolder, idParent, userUid));
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
	
	@RequestMapping(value = "/object/copy", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse copyObjectbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idFolder,
			@RequestHeader(required = false, value = "idParent") String idParent) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					//TODO
					response.setExito(cmis.copyFolder(idFolder, idParent, userUid));
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
	
	@RequestMapping(value = "/folder/copy", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse copyFolderbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idFolder,
			@RequestHeader(required = false, value = "idParent") String idParent) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.copyFolder(idFolder, idParent, userUid));
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
	
	@RequestMapping(value = "/object/id", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ServiceResponse deleteObjectbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idFolder,
			@RequestHeader(required = false, value = "trash") boolean toTrash) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					//TODO:
					response.setExito(cmis.deleteFolder(idFolder, toTrash, userUid));
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
	
	@RequestMapping(value = "/folder/id", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ServiceResponse deleteFolderbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idFolder,
			@RequestHeader(required = false, value = "trash") boolean toTrash) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.deleteFolder(idFolder, toTrash, userUid));
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
	
	@RequestMapping(value = "/object/download/id", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse downloadObjectbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "type") String type,
			HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					//TODO:
					File file = fileService.download(cmis.downloadFolder(id, type, userUid));
					// convertir a Multipart...
			        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			        httpResponse.setContentLength((int)file.length());
			        String mimeType= "application/zip";
			        httpResponse.setContentType(mimeType);
			        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
			        return null;
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		} catch (Exception e) {
			response.setStatus(428);
            return response;
		}
		return response;
	}
	
	@RequestMapping(value = "/folder/download/id", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse downloadFolderbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "type") String type,
			HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					File file = fileService.download(cmis.downloadFolder(id, type, userUid));
					// convertir a Multipart...
			        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			        httpResponse.setContentLength((int)file.length());
			        String mimeType= "application/zip";
			        httpResponse.setContentType(mimeType);
			        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
			        return null;
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		} catch (Exception e) {
			response.setStatus(428);
            return response;
		}
		return response;
	}
	
	@RequestMapping(value = "/object/download/url", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse downloadLinkObjectbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					//TODO
					response.setExito(cmis.downloadFolderLink(id, type, userUid));
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
	
	@RequestMapping(value = "/folder/download/url", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse downloadLinkFolderbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.downloadFolderLink(id, type, userUid));
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
	
	@RequestMapping(value = "/object/download/email", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse downloadEmailObjectbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "email") String email,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					//TODO
					response.setExito(cmis.downloadFolderEmail(id, email, type, userUid));
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
	
	@RequestMapping(value = "/object/share", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse shareObjectbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idFolder) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					//TODO
					response.setExito(cmis.shareFolder(idFolder, userUid));
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
	
	@RequestMapping(value = "/folder/download/email", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse downloadEmailFolderbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "email") String email,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.downloadFolderEmail(id, email, type, userUid));
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
	
	@RequestMapping(value = "/object/template", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getObjectTemplate(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getObjectTemplate(id, type, userUid));
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
	
	@RequestMapping(value = "/object/template", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setObjectTemplate(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "name") String name,
			@RequestHeader(required = false, value = "id") String idObject,
			@RequestHeader(required = false, value = "idTemplate") String idTemplate) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setObjectTemplateById(name, idObject, idTemplate, userUid));
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
	
	@RequestMapping(value = "/object/template", method = RequestMethod.POST, headers = "Accept=application/json")
	public ServiceResponse executeObjectTemplate(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.executeObjectTemplateById(id, userUid));
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
	
	@RequestMapping(value = "/object/template", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ServiceResponse deleteObjectTemplate(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.deleteObjectTemplate(id, userUid));
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
	
	@RequestMapping(value = "/folder/path", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setFolderbyPath(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "path") String path,
			@RequestHeader(required = false, value = "name") String name) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setFolderByPath(name, path, true, userUid));
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
	
	@RequestMapping(value = "/folder/list", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getFolderContent(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getFolderList(id, userUid));
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
	
	@RequestMapping(value = "/document", method = RequestMethod.POST, headers = "Accept=application/json")
	public ServiceResponse setDocument(@RequestParam(required = false, value = "token") String token,
			@RequestParam(required = false, value = "id") String id,
			@RequestParam(required = false, value = "path") String path,
			@RequestParam(required = false, value = "idParent") String idParent,
			@RequestParam(required = false, value = "domain") String idDomain,
			@RequestParam(required = false, value = "documentclass") String documentClass,
			@RequestParam(required = false, value = "properties") String properties,
			@RequestParam(required = false, value = "file") MultipartFile file) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setDocument(id, path,idParent, idDomain, documentClass, properties, file, userUid));
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
	
	/*
	@RequestMapping(value = "/document/content/url/", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getFileUrl(@RequestParam(required = false, value = "token") String token, 
			@RequestParam(required = false, value = "idDoc") String idDoc,
			@RequestParam(required = false, value = "idVersion") String idVersion,
			@RequestParam(required = false, value = "numVersion") String numVersion,
			HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				int version = -1;
				if (numVersion != null) {
					try {
						version = Integer.parseInt(numVersion);
					} catch (Exception e) {}
				}
				JsonNode cmisFile = cmis.getDocumentContentPath(idDoc, idVersion, version, true);
				if (cmisFile == null || cmisFile.size() == 0) {
					response.setStatus(418);
		            return response;
				}
				File file = fileService.download(cmisFile.findValue("path").asText());
				// convertir a Multipart...
		        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		        httpResponse.setContentLength((int)file.length());
		        String mimeType= cmisFile.findValue("mimeType").asText();
		        if(mimeType==null){
		            mimeType = "application/octet-stream";
		        }
		        httpResponse.setContentType(mimeType);
		        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
		        return null;
			} else {
				response.setStatus(6);
	            return response;
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
			return response;
		} catch (Exception e) {
			response.setStatus(402);
            return response;
		}
	}
	*/
	
	@RequestMapping(value = "/document/thumbnail", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getThumbnail(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "idDoc") String idDoc,
			@RequestHeader(required = false, value = "idVersion") String idVersion,
			@RequestHeader(required = false, value = "numVersion") String numVersion,
			HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					int version = -1;
					if (numVersion != null) {
						try {
							version = Integer.parseInt(numVersion);
						} catch (Exception e) {}
					}
					JsonNode cmisFile = cmis.getDocumentThumbnailPath(idDoc, idVersion, version, true, userUid);
					if (cmisFile == null || cmisFile.size() == 0) {
						response.setStatus(418);
			            return response;
					}
					File file = fileService.download(cmisFile.findValue("path").asText());
					// convertir a Multipart...
			        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			        httpResponse.setContentLength((int)file.length());
			        String mimeType= cmisFile.findValue("mimeType").asText();
			        if(mimeType==null){
			            mimeType = "application/octet-stream";
			        }
			        httpResponse.setContentType(mimeType);
			        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
			        return null;
				} else {
					response.setStatus(4);
					return response;
				}
			} else {
				response.setStatus(6);
	            return response;
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
			return response;
		} catch (Exception e) {
			response.setStatus(402);
            return response;
		}
	}
	
	@RequestMapping(value = "/document/content", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getFile(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "idDoc") String idDoc,
			@RequestHeader(required = false, value = "idVersion") String idVersion,
			@RequestHeader(required = false, value = "numVersion") String numVersion,
			HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					int version = -1;
					if (numVersion != null) {
						try {
							version = Integer.parseInt(numVersion);
						} catch (Exception e) {}
					}
					JsonNode cmisFile = cmis.getDocumentContentPath(idDoc, idVersion, version, true, userUid);
					if (cmisFile == null || cmisFile.size() == 0) {
						response.setStatus(418);
			            return response;
					}
					File file = fileService.download(cmisFile.findValue("path").asText());
					// convertir a Multipart...
			        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			        httpResponse.setContentLength((int)file.length());
			        String mimeType= cmisFile.findValue("mimeType").asText();
			        if(mimeType==null){
			            mimeType = "application/octet-stream";
			        }
			        httpResponse.setContentType(mimeType);
			        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
			        return null;
				} else {
					response.setStatus(4);
					return response;
				}
			} else {
				response.setStatus(6);
	            return response;
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
			return response;
		} catch (Exception e) {
			response.setStatus(402);
            return response;
		}
	}
	
	@RequestMapping(value="/document/content", method=RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ServiceResponse setFile(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestParam(required = false, value = "file") MultipartFile file) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setDocument(id, null,null, null, null, null, file, userUid));
					response.setStatus(0);
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
	            return response;
			}
		} catch (Exception e) {
			response.setStatus(407);
		}
		
		
		return response;
	}
	
	@RequestMapping(value = "/document/metadata", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getDocumentMetadata(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "idMetadata") String idMetadata,
			@RequestHeader(required = false, value = "numVersion") String numVersion,
			@RequestHeader(required = false, value = "lastVersion") boolean lastVersion) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					int version = -1;
					if (numVersion != null) {
						try {
							version = Integer.parseInt(numVersion);
						} catch (Exception e) {}
					}
					response.setExito(cmis.getDocumentMetadata(id, idMetadata, version, lastVersion, userUid));
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
	
	@RequestMapping(value = "/document/version", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getDocumentVersion(@RequestHeader(required = false, value = "token") String token,
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "idVersion") String idVersion,
			@RequestHeader(required = false, value = "numVersion") String numVersion,
			@RequestHeader(required = false, value = "lastVersion") boolean lastVersion) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					int version = -1;
					if (numVersion != null) {
						try {
							version = Integer.parseInt(numVersion);
						} catch (Exception e) {}
					}
					response.setExito(cmis.getDocumentContent(id, idVersion, version, lastVersion, userUid));
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
	
	@RequestMapping(value = "/documentClass", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getDocumentClasses(@RequestHeader(required = false, value = "token") String token) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(cmis.getDocumentClasses());
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/documentClass/id", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getDocumentClasses(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				response.setExito(cmis.getDocumentClass(id));
				response.setStatus(0);
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		}
		return response;
	}
	
	@RequestMapping(value = "/object/favorites", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getObjectFavoritesByType(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getObjectFavoritesByType(userUid, type));
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
	
	@RequestMapping(value = "/object/favorites", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ServiceResponse deleteObjectFavoritesById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.deleteObjectFavoritesById(id, userUid));
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
	
	@RequestMapping(value = "/object/favorites", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setObjectFavoritesById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setObjectFavoritesById(userUid, id));
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
	
	@RequestMapping(value = "/document/move", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse moveDocumentbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDoc,
			@RequestHeader(required = false, value = "idParent") String idParent) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.moveDocument(idDoc, idParent, userUid));
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
	
	@RequestMapping(value = "/document/copy", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse copyDocumentbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDoc,
			@RequestHeader(required = false, value = "idParent") String idParent) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.copyDocument(idDoc, idParent, userUid));
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
	
	@RequestMapping(value = "/document/share", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse shareDocumentbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDoc) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.shareDocument(idDoc, userUid));
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
	
	@RequestMapping(value = "/folder/share", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse shareFolderbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idFolder) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.shareFolder(idFolder, userUid));
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
	
	@RequestMapping(value = "/document/version", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setDocumentVersionById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDoc,
			@RequestHeader(required = false, value = "idVersion") String idContentVersion) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setActualDocumentVersion(idDoc, null, idContentVersion, userUid));
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
	
	@RequestMapping(value = "/document/metadata", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setDocumentMetadatabyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDoc,
			@RequestHeader(required = false, value = "idMetadata") String idMetadata) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setActualDocumentVersion(idDoc, idMetadata, null, userUid));
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
	
	@RequestMapping(value = "/document/id", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ServiceResponse deleteDocumentbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDoc,
			@RequestHeader(required = false, value = "trash") boolean toTrash) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.deleteDocument(idDoc, toTrash, userUid));
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
	
	@RequestMapping(value = "/document/checkin", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse checkInDocumentbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDoc) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.checkInDocument(idDoc, userUid));
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
	
	@RequestMapping(value = "/document/checkin", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ServiceResponse cancelCheckInDocumentbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String idDoc) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.cancelCheckInDocument(idDoc, userUid));
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
	
	@RequestMapping(value = "/document/checkout", method = RequestMethod.POST, headers = "Accept=application/json")
	public ServiceResponse checkOutDocument(@RequestParam(required = false, value = "token") String token,
			@RequestParam(required = false, value = "id") String id,
			@RequestParam(required = false, value = "path") String path,
			@RequestParam(required = false, value = "idParent") String idParent,
			@RequestParam(required = false, value = "domain") String idDomain,
			@RequestParam(required = false, value = "documentclass") String documentClass,
			@RequestParam(required = false, value = "properties") String properties,
			@RequestParam(required = false, value = "file") MultipartFile file) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.checkOutDocument(id, path, idParent, idDomain, documentClass, properties, file, userUid));
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
	
	@RequestMapping(value = "/document/download/id", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse downloadDocumentbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "type") String type,
			HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					File file = fileService.download(cmis.downloadDocument(id, type, userUid));
					// convertir a Multipart...
			        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			        httpResponse.setContentLength((int)file.length());
			        String mimeType= "application/zip";
			        httpResponse.setContentType(mimeType);
			        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
			        return null;
				} else {
					response.setStatus(4);
				}
			} else {
				response.setStatus(6);
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
		} catch (Exception e) {
			response.setStatus(428);
            return response;
		}
		return response;
	}
	
	@RequestMapping(value = "/document/download/url", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse downloadLinkDocumentbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.downloadDocumentLink(id, type, userUid));
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
	
	@RequestMapping(value = "/document/download/email", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse downloadEmailDocumentbyId(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "email") String email,
			@RequestHeader(required = false, value = "type") String type) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.downloadDocumentEmail(id, email, type, userUid));
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
	
	@RequestMapping(value = "/stamp/list", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getStampListById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.getStamp(id, null, userUid));
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
	
	@RequestMapping(value = "/stamp/id", method = RequestMethod.PUT, headers = "Accept=application/json")
	public ServiceResponse setStampById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			@RequestHeader(required = false, value = "name") String name,
			@RequestParam(required = false, value = "file") MultipartFile file) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.setStamp(id, name, file, userUid));
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
	
	@RequestMapping(value = "/stamp/id", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ServiceResponse deleteStampById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					response.setExito(cmis.deleteStamp(id, userUid));
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
	
	@RequestMapping(value = "/stamp/id", method = RequestMethod.GET, headers = "Accept=application/json")
	public ServiceResponse getStampById(@RequestHeader(required = false, value = "token") String token, 
			@RequestHeader(required = false, value = "id") String id,
			HttpServletResponse httpResponse) {
		ServiceResponse response = new ServiceResponse();
		try {
			UserSession session = new UserSession();
			session.setSessionId(token);
			session.setHostnameConnected("localhost");
			if (ldapService.validateSession(session)) {
				String userUid = ldapService.getUidFromSession(session);
				if (userUid != null) {
					JsonNode cmisFile = cmis.getStampPath(id, userUid);
					if (cmisFile == null || cmisFile.size() == 0) {
						response.setStatus(418);
			            return response;
					}
					File file = fileService.download(cmisFile.findValue("path").asText());
					// convertir a Multipart...
			        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			        httpResponse.setContentLength((int)file.length());
			        String mimeType= cmisFile.findValue("mimeType").asText();
			        if(mimeType==null){
			            mimeType = "application/octet-stream";
			        }
			        httpResponse.setContentType(mimeType);
			        FileCopyUtils.copy(inputStream, httpResponse.getOutputStream());
			        return null;
				} else {
					response.setStatus(4);
					return response;
				}
			} else {
				response.setStatus(6);
	            return response;
			}
		} catch (AquariusException e) {
			response.setStatus(gutils.getErrorCode(e.getMessage()));
			return response;
		} catch (Exception e) {
			response.setStatus(402);
            return response;
		}
	}
	
	public static void main (String[] args) {
		UserController controller = new UserController();
		//controller.restorePassword("ecm", "adrian.martinez@f2m.com.mx");
		ServiceResponse response = controller.login("demo", "demo");
		String token = response.getExito().toString();
		FileController file = new FileController();
		
		file.getThumbnail(token, "782bd67e-6b89-403c-8823-a7af8f05aec5", null, null, null);
	}
	
}
