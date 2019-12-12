import * as data from '../../assets/config.json';

/*
	SERVICE URL
*/

export class Properties{
	
	//Variable utilizada para definir el hostname
	//private hostName = "http://localhost:8080/aquarius/";
	private hostName = (<any>data).hostName;

	//url de servicio de login
	public loginURL = this.hostName + "login/";

	//url para recuperar contraseña
	public restorePassword = this.hostName + "restorePassword/";

	//url de servicio de logout
	public logoutURL = this.hostName + "logout/";

	//url de obtención de datos de usuario
	public getUserData = this.hostName + "user/domain";

	//url para notificacion de error
	public errorURL = this.hostName + "error/";

	//url Obtener carpetas
	public folderURL = this.hostName + "tree/root";

	//url utilizada para obtener lista de clases documentales
	public documentClass = this.hostName + "documentClass/";

	//url utilizada para obtener documento
	public getDocument = this.hostName + "document/content";

	//url utilizada para obtener lista de documentos ---
	public folderContentURL = this.hostName + "folder/list";

	//url utilizada para obtener la lista de criterios
	public searchType = this.hostName + "searchtype";

	//url para crear carpetas
	public setFolderURL = this.hostName + "folder/id";

	//url para crear documentos
	public setDocumentByIdParent = this.hostName + "document";

	//url utilizada para terminar todas las sesiones de un usuario
	public logoutAll = this.hostName + "logoutAll";

	//url utilizada para obtener el listado de rutas
	public getLstPath = this.hostName + "tree/multiselect/root";
	
	//Url para obtener el domain
	public domainURL = this.hostName + "domain";

	//Url utilizada para obtener información de un nodo
	public nodeInfo = this.hostName + "object/id";

	//Url utilizada para obtener los metadatos de un documento
	public docMetadata = this.hostName + "document/metadata";

	//Url utilizada para obtener la información de versiones de un documento
	public docVersion = this.hostName + "document/version";

	//Url utilizada para obtener la información de propiedades de sistema
	public systemProp = this.hostName + "object/info";

	//Url utilizada para manejo de comentarios
	public comment = this.hostName + "social/comment";

	//Url utilizada para almacenamiento de búsquedas
	public storedSearch = this.hostName + "search/stored";

	//Url utilizada para ejecutar búsqueda
	public search = this.hostName + "search";

	//Url utilizada para obtener thumbnail
	public thumbnail = this.hostName + "document/thumbnail"

	//Url utilizada para obtener lista de sellos
	public lstStamp = this.hostName + "stamp/list";

	//Url utilizada para obtener sellos
	public stamp = this.hostName + "stamp/id";

	//Url utilizada para compartir documentos/carpetas
	public share = this.hostName + "object/share";

	//Url utilizada para todo lo relacionado a templates
	public template = this.hostName + "object/template";

	//Url utilizada para todo lo relacionado a favoritos
	public favorites = this.hostName + "object/favorites";

	//Url utilizada para mover elementos
	public move = this.hostName + "object/move";

	//Url utilizada para copiar elementos
	public copy = this.hostName + "object/copy";

	//Url utilizada para descargas de carpetas o documentos
	public download = this.hostName + "object/download/id";

	//Url utilizada para enviar elementos por correo electrónico
	public email = this.hostName + "folder/download/email";

}