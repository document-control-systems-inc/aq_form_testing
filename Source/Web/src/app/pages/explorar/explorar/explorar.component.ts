import { Component, OnInit, AfterViewInit, Output, EventEmitter, ViewChild, Pipe, PipeTransform, ElementRef } from '@angular/core';
import { NgModel } from '@angular/forms';
import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { UtilitiesServices} from '../../../services/utilities.services';
import { Properties } from '../../../services/properties';
import { ExplorarServices } from '../../../services/explorar.services';
import { SocialServices } from '../../../services/social.services';
import { BusquedaServices } from '../../../services/busqueda.services';
import { TreeModel, NodeEvent, Ng2TreeSettings } from 'ng2-tree';

import { DataTableDirective } from 'angular-datatables';
import { DataTablesModule } from 'angular-datatables';
import { Subject } from 'rxjs/Subject';

import { DropzoneComponent , DropzoneDirective, DropzoneConfigInterface } from 'ngx-dropzone-wrapper';

import * as Chartist from 'chartist';

//jquery
declare const $: any;
//modals
import swal from 'sweetalert2';
//contextmenu
declare const contextMenu:any;

@Component({
  selector: 'explorar',
  templateUrl: './explorar.component.html',
  styleUrls: ['./explorar.component.css'],
  providers: [ UtilitiesServices, Properties, ExplorarServices, PdfViewerServices, SocialServices, BusquedaServices ]
})

export class ExplorarComponent implements OnInit {
  //Listado de elementos seleccionado en tabla
  public lstSelectedElements = [];
  //Variable de correo electrónico
  public email = null;
  //Lista de opciones de descarga
  public lstDownload = null;
  //Lista de paths utilizada en mover/copiar elementos
  public lstPaths;
  //Variable de almacenamiento de idParent para manejo de copiar y mover
  public idParent = null;
  //Variable utilizada para almacenar elemento a copiar/mover
  public elementToCopy;
  //Variable utilizada para almacenar elemento a descargar
  public elementToDownload;
  //Variable utilizada para almacenar el tipo de descarga almacenada
  public objDownload = null;
  //Variable de manejo de componente de loading
  public loading = false;
  //declaración de variable de datatable
  public dataTable;
  //Variable utilizada para alternar entre vistas de tabla
  public view = 'table';
  //declaración de variable de árbol
  public tree: TreeModel;
  //nombre de nueva carpeta a agregar
  public name: any;
  //id de la carpeta seleccionada en el árbol
  public id: any;
  //nombre de la carpeta seleccionada en el arbol
  public folderName;
  //id del domain
  public idDomain;
  //Clase documental a la cual pertenece el documento a subir
  public documentclass;
  //Todas las propiedades de la clase seleccionada
  public claseSeleccionada;
  //Variable en la cual se almacenan los campos de la clase documental seleccionada
  public claseDocumental;
  //Variable temporal de guardado de property
  public newValue:any[];
  //propiedades del documento a subir
  public properties = {};
  //id de elemento seleccionado
  public elementId;
  //documento a crear
  public file;
  //propiedades del árbol
  public settings: Ng2TreeSettings = {
    rootIsVisible: false
  };
  //Variable utilizada para administrar los templates a mostrar
  public template:string='explorar';
  //Variable utilizada para almacenar la lista de clases documentales
  public lstClase;
  //Variable utilizada para guardar el tipo de método para añadir archivo
  public metodoDocumento;
  //Variable temporal utilizada para controlar el evento de doble click
  public tableRequest = 0;
  //Variable utilizada para guardar la lista de id de documentos contenidos en la actual carpeta
  public lstDocumentos = [];
  //Variable utilizada para almacenar la lista de id del folder padre
  public lstParentFolder;
  //Variable utilizada para almacenar el tamaño de la lista de id del folder padre
  public lstParentFolderSize;
  //Variable utilizada para almacenar el nombre de la carpeta seleccionada en la tabla
  public selectedFolderName;
  //Variable utilizada para almacenar los metadatos de un documento
  public metadata;
  //Variable utilizada para almacenar las propiedades de sistema de un documento/carpeta
  public systemProperties = {
    'name': null,
    'numDocuments':null,
    'numFolders':null,
    'createdBy':null,
    'createdOn':{
      'time':null
    },
    'id':null,
    'contentVersion':null,
    'metadataVersion':null,
    'size':null,
    'modifiedBy':null,
    'modifiedOn':{
      'time':null
    },
    'mimeType':null
  };
  //Variable utilizada para almacenar el historial de versiones de un documento
  public historial;
  //Configuración de dropzone
  public config: DropzoneConfigInterface = {
    clickable: true,
    maxFiles:1
  };
  //Elemento de dropzone
  @ViewChild(DropzoneComponent) componentRef: DropzoneComponent;
  //Elemento de tree
  @ViewChild('treeComponent') public treeComponent;
  //Manejo de paginado y filtrado en tabla
  public showPagination = true;
  public pages = [];
  public selectedPage = 1;
  public filter;

	constructor(private _busquedaServices:BusquedaServices, private _socialServices:SocialServices, private _pdfViewerServices:PdfViewerServices, private _utilitiesServices: UtilitiesServices, private _explorarServices: ExplorarServices, private el: ElementRef){}

  public reset() {
    this.componentRef.directiveRef.reset();
  }

	ngOnInit() {
    //Obtener lista de clases documentales
    this.getDocument();
  }

  //Evento para obtener el id de carpeta del árbol
  public selectedFolder(e: NodeEvent): void {
    this.filter = '';
    this.id = e.node.node.id;
    this.folderName = e.node.node.value;
    this.getParentFolder(this.id);
    let that = this;
    setTimeout(function(){
      that.getFolderContent(that.id);
    },10);
    setTimeout(function(){
      that.view = 'table';
    },130);
    let node = this.treeComponent.getControllerByNodeId(this.id);
    node.expand();
  }

  //Modal de propiedades de documento
  propertiesModal(index){
      this.metadata = null;
      this.systemProperties = {
        'name': null,
        'numDocuments':null,
        'numFolders':null,
        'createdBy':null,
        'createdOn':{
          'time':null
        },
        'id':null,
        'contentVersion':null,
        'metadataVersion':null,
        'size':null,
        'modifiedBy':null,
        'modifiedOn':{
          'time':null
        },
        'mimeType':null
      };
      this.historial = null;
      if(this.dataTable.dataRows[index][6]==='document'){
        this.getDocMetadata(this.dataTable.dataRows[index][5]);
      }else{
        this.getSystemProperties(this.dataTable.dataRows[index][5]);
      }
      let that = this;
      let docClass;
      setTimeout(function(){
        if(that.dataTable.dataRows[index][6]==='document'){
          swal({
              title: 'Información de documento',
              html: 
              '<div class="card">'+
                '<div class="card-header card-header-icon" data-background-color="blue">'+
                    '<i class="material-icons">description</i>'+
                '</div>'+
                '<h4 class="card-title text-left" style="margin-top: 10px;margin-bottom: 15px;">Propiedades de documento</h4>'+
                '<table>'+
                    '<tbody id="properties">'+
                      
                    '</tbody>'+
                '</table>'+
              '</div>'+
              '<div class="card">'+
                '<div class="card-header card-header-icon" data-background-color="green">'+
                    '<i class="material-icons">settings</i>'+
                '</div>'+
                '<h4 class="card-title text-left" style="margin-top: 10px;margin-bottom: 15px;">Propiedades de sistema</h4>'+
                '<table>'+
                    '<tbody>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Versión'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.contentVersion + '.' + that.systemProperties.metadataVersion +'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Tamaño'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.size+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Modificado por'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.modifiedBy+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Modificado el'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.convertDate(that.systemProperties.modifiedOn.time)+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Añadido por'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.createdBy+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Añadido el'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.convertDate(that.systemProperties.createdOn.time)+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label>'+
                              'ID'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.id+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Tipo MIME'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.mimeType+'</td>'+
                      '</tr>'+
                  '</tbody>'+
                '</table>'+
              '</div>'+
              '<div class="card">'+
                  '<div class="card-header card-header-icon" data-background-color="red">'+
                        '<i class="material-icons">schedule</i>'+
                  '</div>'+
                  '<h4 class="card-title text-left" style="margin-top: 10px;margin-bottom: 15px;">Historial de versiones</h4>'+
                  '<table>'+
                    '<thead>'+
                      '<th class="col-xs-1">'+
                        '<label translate class="text-left">'+
                            'Versión'+
                        '</label>'+
                      '</th>'+
                      '<th class="col-xs-4">'+
                        '<label translate class="text-left">'+
                            'Autor'+
                        '</label>'+
                      '</th>'+
                      '<th class="col-xs-4">'+
                        '<label translate class="text-left">'+
                            'Fecha'+
                        '</label>'+
                      '</th>'+
                      '<th class="col-xs-3"></th>'+
                    '</thead>'+
                    '<tbody id="historial">'+
                      
                    '</tbody>'+
                '</table>'+
              '</div>',
              showCancelButton: true,
              showConfirmButton: false,
              allowOutsideClick: true,
              allowEscapeKey: true,
              cancelButtonText: "Regresar"
          });

          //Pintar los datos de propiedades de documento
          for(let i = 0; i < that.lstClase.length; i++){
            if(that.lstClase[i].id === that.metadata[0].documentClass){
              docClass = that.lstClase[i].label;
              $('#properties').append(
                '<tr>'+
                  '<td class="col-xs-5 text-left">'+
                    '<label translate>'+
                        'Clase documental'+
                    '</label>'+
                  '</td>'+
                  '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+docClass+'</td>'+
                '</tr>'
              );
              for(let iteratorField = 0; iteratorField < that.lstClase[i].fields.length; iteratorField++){
                if(that.metadata[0].properties[that.lstClase[i].fields[iteratorField].name]){
                  let key = that.lstClase[i].fields[iteratorField].label;
                  $('#properties').append(
                    '<tr>'+
                      '<td class="col-xs-5 text-left">'+
                        '<label translate>'+
                            key+
                        '</label>'+
                      '</td>'+
                      '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+
                        that.metadata[0].properties[that.lstClase[i].fields[iteratorField].name]+
                      '</td>'+
                    '</tr>'
                  );
                }
              }
            }
          }

          //Pintar los datos de historial de versiones
          for(let i = 0; i < that.historial.length; i++){
            $('#historial').append(
              '<tr>'+
                '<td class="text-center" style="font-size: 15px;padding-bottom: 5px;">'+
                    '1' +
                '</td>'+
                '<td style="font-size: 15px;padding-bottom: 5px;">'+
                    that.historial[i].createdBy+
                '</td>'+
                '<td style="padding-right: 5px;font-size: 15px;padding-bottom: 5px;">'+
                    that.convertDate(that.historial[i].createdOn.time)+
                '</td>'+
                '<td>'+
                  '<button type="button" class="btn btn-primary btn-xs">'+
                    '<i class="material-icons">visibility</i>'+
                  '</button>'+
                  '<button type="button" class="btn btn-primary btn-xs">'+
                    '<i class="material-icons">replay</i>'+
                  '</button>'+
                '</td>'+
              '</tr>'
            );
          }

        }else if(that.dataTable.dataRows[index][6]==='folder'){
          swal({
              title: 'Información de carpeta',
              html: 
              '<div class="card">'+
                '<div class="card-header card-header-icon" data-background-color="green">'+
                    '<i class="material-icons">settings</i>'+
                '</div>'+
                '<h4 class="card-title text-left" style="margin-top: 10px;margin-bottom: 15px;">Propiedades de sistema</h4>'+
                '<table>'+
                    '<tbody>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Nombre'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.name+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Número documentos'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.numDocuments+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Número subcarpetas'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.numFolders+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Añadido por'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.createdBy+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label translate>'+
                              'Añadido el'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.convertDate(that.systemProperties.createdOn.time)+'</td>'+
                      '</tr>'+
                      '<tr>'+
                        '<td class="col-xs-5 text-left">'+
                          '<label>'+
                              'ID'+
                          '</label>'+
                        '</td>'+
                        '<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.id+'</td>'+
                      '</tr>'+
                  '</tbody>'+
                '</table>'+
              '</div>',
              showCancelButton: true,
              showConfirmButton: false,
              allowOutsideClick: true,
              allowEscapeKey: true,
              cancelButtonText: "Regresar"
          });
        }
        
        //Se le da estilo al modal
        $(".swal2-modal.swal2-show").css('background-color', '#eeeeee');
          
      },1000);
  }

  //-------------------------------------BLOQUE DE TABLA------------------------------------------------------------------

  //Función para ir a página anterior
  prevPage(){
    if(this.selectedPage>1){
      this.selectedPage--;
      if(this.view!=='table'){
        this.getLstThumbnails(this.selectedPage);
      }  
    }
  }

  //Función para ir a la página siguiente
  nextPage(){
    if(this.selectedPage<this.pages.length){
      this.selectedPage++;
      if(this.view!=='table'){
        this.getLstThumbnails(this.selectedPage);
      }
    }
  }

  //-------------------------------------BLOQUE DE TABLA------------------------------------------------------------------

  //Función utilizada para obtener información acerca de clases documentales
  getDocument(){
    this.loading = true;
    this._pdfViewerServices.documentClass(localStorage.getItem('token')).subscribe(
      result => {
          if(result.status===0){
            this.lstClase = result.exito;
            let that = this;
            setTimeout(function(){
              //Obtener información del árbol
              that.getFolder();
            },50);      
          }else{
              this.loading = false;
              this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                  if(result===true){
                      let that = this;
                      setTimeout(function(){
                          that.getDocument();
                      },1000);
                  }                        
              });
          }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Función utilizada para asignar una nueva propiedad
  setNewProperty(index){
    let property = this.claseDocumental[index].name;
    this.properties[property] = this.newValue[index]; 
  }

  //Función utilizada para convertir fecha seleccionada a timestamp
  convertDateToTimestamp(index){
    let property = this.claseDocumental[index].name;
    let selectedDate = this.newValue[index];
    this.properties[property] = Math.round(selectedDate.getTime());
  }

  //Función utilizada para limpiar campo de fechas
  cleanData(index){
    let property = this.claseDocumental[index].name;
    this.newValue[index] = "";
    this.properties[property] = "";
  }

  //Función utilizada para limpiar campos de agregar documento y redirigir a pantalla de explorar
  regresarAExplorar(){
    this.lstSelectedElements = [];
    this.email = null;
    this.elementToCopy = null;
    this.elementToDownload = null;
    this.objDownload = null;
    this.idParent = null;
    this.metodoDocumento = null;
    this.claseSeleccionada = null;
    this.claseDocumental = null;
    this.documentclass = null;
    this.newValue = [];
    this.properties = {};
    this.file = null;
    $('#upload-file-info').html('Archivo no seleccionado');
    this.template='explorar';
    this.tableRequest = 0;
    this.getFolderContent(this.id);
  }

  //Función utilizada para obtener idDomain
  getDomain(){
      this._explorarServices.getDomain(localStorage.getItem('token')).subscribe(
          result => {
              if(result.status===0){
                this.idDomain = result.exito[0].id;
                let that = this;
                setTimeout(function(){
                  that.getLstPath();
                },100);
              }else{
                  this.loading = false;
                  this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                      if(result===true){
                          let that = this;
                          setTimeout(function(){
                              that.getDomain();
                          },1000);
                      }                        
                  });
              }
          }, 
          error => {
              this.loading = false;
              var errorMessage = <any> error;
              console.log(errorMessage);
          }
      );
  }

  //Función utilizada para cambiar clase documental y el estilo a los datepicker
  setDocClass(){
    this.properties = {};
    this.newValue = [];
    this.documentclass = this.claseSeleccionada.id;
    this.claseDocumental = this.claseSeleccionada.fields;
    for(let i = 0; i < this.claseDocumental.length; i++){
      this.newValue.push("");
    }
    setTimeout(function(){
      $('.owl-dateTime.owl-widget').css('width', '152');
    },100);
  }

  //Función utilizada para regresar al folder padre
  returnToParentFolder(){
    let oldParentFolder = this.lstParentFolder[this.lstParentFolderSize];
    this.id = oldParentFolder;
    this.getParentFolder(oldParentFolder);
    let that = this;
    setTimeout(function(){
      //that.tableRequest = 0;
      that.getFolderContent(oldParentFolder);
    },10);
    let node = this.treeComponent.getControllerByNodeId(oldParentFolder);
    node.select();
    //node.collapse();
  }

  //Función utilizada para obtener el id del folder padre
  getParentFolder(id){
    this.loading = true;
    this._explorarServices.getParentFolder(localStorage.getItem('token'), id).subscribe(
        result => {
            if(result.status===0){
                this.loading = false;
                this.lstParentFolder = result.exito.parent;
                this.lstParentFolderSize = this.lstParentFolder.length-1;
                this.folderName = result.exito.properties.name;
            }else{
                this.loading = false;
                this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                    if(result===true){
                        let that = this;
                        setTimeout(function(){
                            that.getParentFolder(id);
                        },1000);
                    }                        
                });
            }
        },
        error => {
            this.loading = false;
            var errorMessage = <any> error;
            console.log(errorMessage);
        }
    );
  }

  //Función utilizada para obtener los elementos que contiene una carpeta
  getFolderContent(id){
      this.loading = true;
      this._explorarServices.getFolderContent(localStorage.getItem('token'), localStorage.getItem('domain'), id).subscribe(
          result => {
              if(result.status===0){
                  this.loading = false;
                  let that = this;
                  this.dataTable = result.exito;
                  //Contador de consultas de tabla
                  this.tableRequest++;
                  //Se configura el paginado
                  this.pages = [];
                  this.selectedPage = 1;
                  for(let i = 0; i < this.dataTable.dataRows.length/10; i++){
                    this.pages.push("");
                  }
                  //Se añade evento de doble click
                  if (this.dataTable.dataRows.length<1) {
                    this.tableRequest = 0;
                  }
                  if(this.tableRequest<=1){
                    setTimeout(function(){
                      //Se añade el evento de doble click 
                      $('#datatable').on('dblclick touchstart', 'tr.clickedRow', function(){ 
                        let nombreSeleccionado = this.cells[1].childNodes[1].childNodes[6].innerText;
                        that.goToDocViewer(nombreSeleccionado);
                      });
                    },100);
                  }                 
              }else{
                  this.loading = false;
                  this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                      if(result===true){
                          let that = this;
                          setTimeout(function(){
                              that.getFolderContent(id);
                          },1000);
                      }                        
                  });
              }
          }, 
          error => {
              this.loading = false;
              var errorMessage = <any> error;
              console.log(errorMessage);
          }
      );
  }

  goToDocViewer(name){
    let nombreSeleccionado = name;
    let index;
    for(let i = 0; i < this.dataTable.dataRows.length; i++){
      if(nombreSeleccionado===this.dataTable.dataRows[i][0]){
        index = i;
      }
    }
    if(this.dataTable.dataRows[index][6]==='folder'){
      this.filter = '';
      this.getParentFolder(this.dataTable.dataRows[index][5]);
      let that = this;
      setTimeout(function(){
        that.id = that.dataTable.dataRows[index][5];
        that.getFolderContent(that.dataTable.dataRows[index][5]);
        let ids = that.dataTable.dataRows[index][5];
        let node = that.treeComponent.getControllerByNodeId(ids);
        node.select();
      },10);

    }else if(this.dataTable.dataRows[index][6]==='document'){
      this.elementId = this.dataTable.dataRows[index][5];
      this.lstDocumentos = [];
      for(let i = 0; i < this.dataTable.dataRows.length; i++){
        if(this.dataTable.dataRows[i][6]==='document'){
          this.lstDocumentos.push(this.dataTable.dataRows[i][5]);
        }
      }
      this.template = "viewer";
    }
  }

  getLstThumbnails(page){
    this.loading = true;
    let thumbnailIndex;
    if((this.selectedPage-1)*12 < this.dataTable.dataRows.length){
      thumbnailIndex = this.dataTable.dataRows.length;
    }else{
      thumbnailIndex = this.selectedPage*12;
    }
    for(let i = (this.selectedPage - 1) * 12; i < thumbnailIndex; i++){
      this.getThumbnail(i);
    }
    let that = this;
    setTimeout(function(){
      //Se añade el evento de doble click
      $('#thumbnails').off('dblclick touchstart').on('dblclick touchstart', 'div.thumbMargin', function(){ 
        let nombreSeleccionado = this.childNodes[1].innerText;
        let index;
        for(let i = 0; i < that.dataTable.dataRows.length; i++){
          if(nombreSeleccionado===that.dataTable.dataRows[i][0]){
            index = i;
          }
        }
        if(that.dataTable.dataRows[index][6]==='folder'){
          that.filter = '';
          that.getParentFolder(that.dataTable.dataRows[index][5]);
          setTimeout(function(){
            that.id = that.dataTable.dataRows[index][5];
            that.getFolderContent(that.dataTable.dataRows[index][5]);
            let ids = that.dataTable.dataRows[index][5];
            let node = that.treeComponent.getControllerByNodeId(ids);
            node.select();
          },10);

        }else if(that.dataTable.dataRows[index][6]==='document'){
          that.elementId = that.dataTable.dataRows[index][5];
          that.lstDocumentos = [];
          for(let i = 0; i < that.dataTable.dataRows.length; i++){
            if(that.dataTable.dataRows[i][6]==='document'){
              that.lstDocumentos.push(that.dataTable.dataRows[i][5]);
            }
          }
          that.template = "viewer";
        }
      });
    },100);
    this.view = 'thumbnail';
    this.loading = false;
  }

  //Función utilizada para obtener la información del árbol
  getThumbnail(index){
      let id = this.dataTable.dataRows[index][5];
      this._explorarServices.getThumbnail(localStorage.getItem('token'), id).subscribe(
          result => {
              let blob = new Blob([result], {type:'image/png'});
              var reader = new FileReader();
              reader.readAsDataURL(blob); 
              let that = this;
              reader.onloadend = function() {
                that.dataTable.dataRows[index].push(reader.result);        
              }
          },
          error => {
              this.loading = false;
              var errorMessage = <any> error;
              console.log(errorMessage);
          }
      );
  }

  //Función utilizada para obtener la información del árbol
  getFolder(){
      this._explorarServices.getFolder(localStorage.getItem('token')).subscribe(
          result => {
              if(result.status===0){
                  this.tree = result.exito;
                  let that = this;
                  setTimeout(function(){
                    //Obtener idDomain
                    that.getDomain();
                  },50);
              }else{
                  this.loading = false;
                  this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                      if(result===true){
                          let that = this;
                          setTimeout(function(){
                              that.getFolder();
                          },1000);
                      }                        
                  });
              }
          },
          error => {
              this.loading = false;
              var errorMessage = <any> error;
              console.log(errorMessage);
          }
      );
  }

  //Función utilizada para añadir una nueva carpeta
  setFolder(){
      this.loading = true;
      if(this.id===null||this.id===undefined){
          this.id = this.idDomain;
      }
      this._explorarServices.setFolder(localStorage.getItem('token'), this.idDomain , this.name, this.id).subscribe(
          result => {
              if(result.status===0){  
                  this.loading = false;           
                  this.getFolderContent(this.id);
                  $.notify({
                      icon: 'notifications',
                      message: 'Carpeta agregada correctamente.'
                  }, {
                      type: 'success',
                      timer: 500,
                      delay: 2000,
                      placement: {
                          from: 'top',
                          align: 'right'
                      }
                  });
                  let that = this;
                  setTimeout(function(){
                    that.getFolder();
                  },100);
              }else{
                  this.loading = false;
                  this._utilitiesServices.showSwalError(result.status, null);
              }
          },
          error => {
              this.loading = false;
              var errorMessage = <any> error;
              console.log(errorMessage);
          }
      );
  }

  //Función utilizada para obtener el historial de versiones de un documento
  getContentVersion(id){
    this._explorarServices.getHistorial(localStorage.getItem('token'), id).subscribe(
      result => {
        if(result.status===0){
          this.historial = result.exito;
          let that = this;
          setTimeout(function(){
            that.getSystemProperties(id);
          },100);
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, (error, result): void => {
            if(result===true){
              let that = this;
              setTimeout(function(){
                that.getContentVersion(id);
              },1000);
            }                        
          });
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Función utilizada para obtener las propiedades de sistema de un documento
  getSystemProperties(id){
    this.loading = true;
    this._explorarServices.getSystemProperties(localStorage.getItem('token'), id).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          this.systemProperties = result.exito.properties;
        }else{
          this._utilitiesServices.showSwalError(result.status, (error, result): void => {
            this.loading = false;
            if(result===true){
              let that = this;
              setTimeout(function(){
                that.getSystemProperties(id);
              },1000);
            }                        
          });
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Función utilizada para obtener los metadatos de un documento
  getDocMetadata(id){
    this.loading = true;
    this._explorarServices.getDocMetadata(localStorage.getItem('token'), id).subscribe(
      result => {
        if(result.status===0){
          this.metadata = result.exito;
          let that = this;
          setTimeout(function(){
            that.getContentVersion(id);
          },100);
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, (error, result): void => {
            if(result===true){
              let that = this;
              setTimeout(function(){
                that.getDocMetadata(id);
              },1000);
            }                        
          });
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Modal de agregar carpeta
  folderModal(){
      let that = this;
      swal({
          title: 'Nueva carpeta',
          html: '<div class="form-inline">'+
                  '<label> Nombre: </label>'+ 
                    '<div class="form-group" style="margin-top: 0px;">'+
                      '<input type="text" class="form-control" id="folderName" required="required">'+
                    '</div>'+
                '</div>',
          showCancelButton: true,
          allowOutsideClick: false,
          allowEscapeKey: false,
          confirmButtonClass: "btn-danger",
          confirmButtonText: "Agregar"
      }).then(function(){
          // function when confirm button clicked
          that.name = $('#folderName').val();
          that.setFolder();
      });
  }

  //Función utilizada para añadir un nuevo archivo
  getDocumentURL(){
      this.loading = true;
      if(this.id!==null||this.id!==undefined){
        this._explorarServices.setDocumentByIdParent(localStorage.getItem('token'), this.id, this.idDomain, this.documentclass, this.properties, this.file).subscribe(
            result => {
                if(result.status===0){
                    this.loading = false;
                    this.regresarAExplorar();
                    this.getFolderContent(this.id);
                    $.notify({
                        icon: 'notifications',
                        message: 'Documento agregado correctamente.'
                    }, {
                        type: 'success',
                        timer: 500,
                        delay: 2000,
                        placement: {
                            from: 'top',
                            align: 'right'
                        }
                    });
                }else{
                    this.loading = false;
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                            let that = this;
                            setTimeout(function(){
                                that.getDocumentURL();
                            },1000);
                        }                        
                    });
                }
            }, 
            error => {
                this.loading = false;
                var errorMessage = <any> error;
                console.log(errorMessage);
            }
        );
      }else{

      }
  }

  //Evento de botón de regresar a pantalla de explorar
  regresar(regresarEvent: any){
    this.template = regresarEvent;
    let that = this;
    setTimeout(function(){
      //Se añade el evento de doble click 
      $('#datatable').on('dblclick touchstart', 'tr.clickedRow', function(){ 
        //let index = this.rowIndex - 1;
        //let nombreSeleccionado = that.dataTable.dataRows[index][0];
        let nombreSeleccionado = this.cells[1].childNodes[1].childNodes[6].innerText;
        let index;
        for(let i = 0; i < that.dataTable.dataRows.length; i++){
          if(nombreSeleccionado===that.dataTable.dataRows[i][0]){
            index = i;
          }
        }
        if(that.dataTable.dataRows[index][6]==='folder'){
          that.filter = '';
          //that.folderName = that.dataTable.dataRows[index][0];
          that.getParentFolder(that.dataTable.dataRows[index][5]);
          setTimeout(function(){
            that.id = that.dataTable.dataRows[index][5];
            that.getFolderContent(that.dataTable.dataRows[index][5]);
            let ids = that.dataTable.dataRows[index][5];
            let node = that.treeComponent.getControllerByNodeId(ids);
            node.seleclt();
          },10);

        }else if(that.dataTable.dataRows[index][6]==='document'){
          that.elementId = that.dataTable.dataRows[index][5];
          that.lstDocumentos = [];
          for(let i = 0; i < that.dataTable.dataRows.length; i++){
            if(that.dataTable.dataRows[i][6]==='document'){
              that.lstDocumentos.push(that.dataTable.dataRows[i][5]);
            }
          }
          that.template = "viewer";
        }
      });
      },100);
    this.tableRequest++;
  }

  getFile(e){
    this.file = e[0];
  }

  addDocument(){
    let that = this;
    this.template = "añadir";
    $(document).on('change', '#my-file-selector', function(){
      $('#upload-file-info').html(this.files[0].name);
      that.file = this.files[0];
    });    
  }

  /*/////////////////////////////////////////////////////////////////////////////////
              BLOQUE DE CONTEXT MENU
  /*/////////////////////////////////////////////////////////////////////////////////

  //Función utilizada para compartir documentos o carpetas
  share(id){
    this.loading = true;
    this._explorarServices.share(localStorage.getItem('token'), id).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          $.notify({
              icon: 'notifications',
              message: 'El elemento se ha compartido correctamente.'
          }, {
              type: 'success',
              timer: 500,
              delay: 2000,
              placement: {
                  from: 'top',
                  align: 'right'
              }
          });
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, (error, result): void => {
            if(result===true){
              let that = this;
              setTimeout(function(){
                that.share(id);
              },1000);
            }                        
          });
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Función utilizada para añadir documentos o carpetas a favoritos
  setFavorites(id){
    this.loading = true;
    this._explorarServices.setFavorites(localStorage.getItem('token'), id).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          $.notify({
              icon: 'notifications',
              message: 'El elemento fue agregado a favoritos.'
          }, {
              type: 'success',
              timer: 500,
              delay: 2000,
              placement: {
                  from: 'top',
                  align: 'right'
              }
          });
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, (error, result): void => {
            if(result===true){
              let that = this;
              setTimeout(function(){
                that.setFavorites(id);
              },1000);
            }                        
          });
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Función utilizada para eliminar documentos o carpetas
  deleteNode(id){
    this.loading = true;
    this._explorarServices.deleteNode(localStorage.getItem('token'), id).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          $.notify({
              icon: 'notifications',
              message: 'El elemento se ha eliminado correctamente.'
          }, {
              type: 'success',
              timer: 500,
              delay: 2000,
              placement: {
                  from: 'top',
                  align: 'right'
              }
          });
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, (error, result): void => {
            if(result===true){
              let that = this;
              setTimeout(function(){
                that.deleteNode(id);
              },1000);
            }                        
          });
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Modal de cambiar nombre de documento o carpeta
  changeNameModal(row){
      let that = this;
      swal({
          title: 'Cambiar nombre',
          html: '<div class="form-inline">'+
                  '<label> Nombre actual: </label>'+ 
                    '<div class="form-group" style="margin-top: 0px;">'+
                      '<input type="text" class="form-control" id="actualName" value="'+row[0]+'" disabled>'+
                    '</div>'+
                '</div>'+
                '<div class="form-inline">'+
                  '<label> Nuevo nombre: </label>'+ 
                    '<div class="form-group" style="margin-top: 0px;">'+
                      '<input type="text" class="form-control" id="newName" required="required">'+
                    '</div>'+
                '</div>',
          showCancelButton: true,
          allowOutsideClick: false,
          allowEscapeKey: false,
          confirmButtonClass: "btn-danger",
          confirmButtonText: "Agregar"
      }).then(function(){
          // function when confirm button clicked
          let name = $('#newName').val();
          that.changeName(row[6], name);
      });
  }

  //Función de cambio de nombre
  changeName(id, name){
    this.loading = true;
    this._explorarServices.changeName(localStorage.getItem('token'), id, name).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          $.notify({
              icon: 'notifications',
              message: 'El nombre del elemento ha sido cambiado correctamente.'
          }, {
              type: 'success',
              timer: 500,
              delay: 2000,
              placement: {
                  from: 'top',
                  align: 'right'
              }
          });
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, (error, result): void => {
            if(result===true){
              let that = this;
              setTimeout(function(){
                that.changeName(id, name);
              },1000);
            }                        
          });
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Modal de creación de template
  setTemplateModal(row){
      let that = this;
      swal({
          title: 'Crear plantilla',
          html: '<div class="form-inline">'+
                  '<label> Documento seleccionado: </label>'+ 
                    '<div class="form-group" style="margin-top: 0px;">'+
                      '<input type="text" class="form-control" id="actualName" value="'+row[0]+'" disabled>'+
                    '</div>'+
                '</div>'+
                '<div class="form-inline">'+
                  '<label> Nombre plantilla: </label>'+ 
                    '<div class="form-group" style="margin-top: 0px;">'+
                      '<input type="text" class="form-control" id="templateName" required>'+
                    '</div>'+
                '</div>',
          showCancelButton: true,
          allowOutsideClick: false,
          allowEscapeKey: false,
          confirmButtonClass: "btn-danger",
          confirmButtonText: "Agregar"
      }).then(function(){
          // function when confirm button clicked
          let name = $('#templateName').val();
          that.setTemplate(row[6], name);
      });
  }

  //Función de creación de template
  setTemplate(id, name){
    this.loading = true;
    this._explorarServices.changeName(localStorage.getItem('token'), id, name).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          $.notify({
              icon: 'notifications',
              message: 'La plantilla ha sido creada correctamente.'
          }, {
              type: 'success',
              timer: 500,
              delay: 2000,
              placement: {
                  from: 'top',
                  align: 'right'
              }
          });
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, (error, result): void => {
            if(result===true){
              let that = this;
              setTimeout(function(){
                that.setTemplate(id, name);
              },1000);
            }                        
          });
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Modal de comentario(en documento)
  setCommentModal(row){
      let that = this;
      swal({
          title: 'Realizar comentario',
          html: '<div class="input-group comment">'+
                  '<div class="form-group" style="margin-top: 0px;padding-bottom: 0px;background-color: white; border-bottom-left-radius: 3px; border-top-left-radius: 3px;">'+
                    '<textarea class="form-control" rows="1" style="resize:none;height:80px;" id="comment" autosize></textarea>'+
                  '</div>'+
                  '<span class="input-group-addon btn btn-primary" style="background-color: #9c27b0;padding-top: 0px;" id="commentButton">'+
                    '<i class="fa fa-paper-plane fa-2x" aria-hidden="true"></i>'+
                  '</span>'+
                '</div>',
          showConfirmButton: false,
          showCancelButton: false,
          allowOutsideClick: true,
          allowEscapeKey: true,
          confirmButtonClass: "btn-danger",
          confirmButtonText: "Agregar"
      });
      setTimeout(function(){
        $('.comment').on('click', 'span#commentButton', function(){ 
          let comment = $('#comment').val();
          that.setComment(row[5], comment);
        });
      },300);
  }

  setComment(id, comment){
    this.loading = true;
    this._socialServices.setComment(localStorage.getItem('token'), id, comment).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          swal.close();
          $.notify({
              icon: 'notifications',
              message: 'Comentario publicado correctamente.'
          }, {
              type: 'success',
              timer: 500,
              delay: 2000,
              placement: {
                  from: 'top',
                  align: 'right'
              }
          });
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, null);
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  getLstPath(){
    this._busquedaServices.getLstPath(localStorage.getItem('token')).subscribe(
      result => {
          if(result.status===0){
            this.lstPaths = result.exito;
            this.loading = false;
          }else{
            this.loading = false;
            this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                if(result===true){
                  let that = this;
                    setTimeout(function(){
                        that.getLstPath();
                    },1000);
                }                        
            });
          }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  toMoveElement(row){
    this.template = 'move';
    this.elementToCopy = row;
  }

  toCopyElement(row){
    this.template = 'copy';
    this.elementToCopy = row;
  }

  selectedNodeCopy(event){
    this.idParent = event.node.node.id;
  }

  moveElement(){
    this.loading = true;
    this._explorarServices.moveElement(localStorage.getItem('token'), this.elementToCopy[5], this.idParent.id).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          this.regresarAExplorar();
          $.notify({
              icon: 'notifications',
              message: 'Se ha cambiado la ubicación del elemento.'
          }, {
              type: 'success',
              timer: 500,
              delay: 2000,
              placement: {
                  from: 'top',
                  align: 'right'
              }
          });
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, null);
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  copyElement(){
    this.loading = true;
    this._explorarServices.copyElement(localStorage.getItem('token'), this.elementToCopy[5], this.idParent.id).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          this.regresarAExplorar();
          $.notify({
              icon: 'notifications',
              message: 'Elemento copiado correctamente.'
          }, {
              type: 'success',
              timer: 500,
              delay: 2000,
              placement: {
                  from: 'top',
                  align: 'right'
              }
          });
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, null);
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Redirige a pantalla de descarga
  toDownload(row){
      this.lstDownload = [];
      if(row[6]==='folder'){
        this.lstDownload = [{
          id:'content',
          value:'ZIP de contenido'
        },{
          id:'metadata',
          value:'Información como CSV'
        },{
          id:'contentMetadata',
          value:'Contenido e información'
        },{
          id:'list',
          value:'Lista'
        }]
      }else if(row[6]==='document'){
        this.lstDownload = [{
          id:'zipContent',
          value:'ZIP de contenido'
        },{
          id:'zipContentMetadata',
          value:'ZIP con propiedades'
        },{
          id:'original',
          value:'Contenido original'
        },{
          id:'csv',
          value:'Información como CSV'
        },{
          id:'pdf',
          value:'PDF'
        }]
      }
      this.template = 'download';
      this.elementToDownload = row;
  }

  //Redirige a pantalla de enviar email
  toSendEmail(row){
      this.lstDownload = [];
      if(row[6]==='folder'){
        this.lstDownload = [{
          id:'content',
          value:'ZIP de contenido'
        },{
          id:'metadata',
          value:'Información como CSV'
        },{
          id:'contentMetadata',
          value:'Contenido e información'
        },{
          id:'list',
          value:'Lista'
        }]
      }else if(row[6]==='document'){
        this.lstDownload = [{
          id:'zipContent',
          value:'ZIP de contenido'
        },{
          id:'zipContentMetadata',
          value:'ZIP con propiedades'
        },{
          id:'original',
          value:'Contenido original'
        },{
          id:'csv',
          value:'Información como CSV'
        },{
          id:'pdf',
          value:'PDF'
        }]
      }
      this.template = 'email';
      this.elementToDownload = row;
  }

  //Se definen id y se llama al método de descarga
  multipleSendEmail(){
    let id:String = '';
    for(let i = 0; i < this.lstSelectedElements.length; i++){
      id = id + this.lstSelectedElements[i][5] + '|';
    }
    id = id.slice(0, -1);
    this.sendEmail(id);
  }

  sendEmail(id){
    this.loading = true;
    this._explorarServices.sendEmail(localStorage.getItem('token'), id, this.email, this.objDownload.id).subscribe(
      result => {
        if(result.status===0){
          this.loading = false;
          this.regresarAExplorar();
          $.notify({
              icon: 'notifications',
              message: 'Correo enviado correctamente.'
          }, {
              type: 'success',
              timer: 500,
              delay: 2000,
              placement: {
                  from: 'top',
                  align: 'right'
              }
          });
        }else{
          this.loading = false;
          this._utilitiesServices.showSwalError(result.status, null);
        }
      },
      error => {
        this.loading = false;
        var errorMessage = <any> error;
        console.log(errorMessage);
      }
    );
  }

  //Se definen id y se llama al método de descarga
  multipleDownload(){
    let id:String = '';
    for(let i = 0; i < this.lstSelectedElements.length; i++){
      id = id + this.lstSelectedElements[i][5] + '|';
    }
    id = id.slice(0, -1);
    this.download(id);
  }

  //Método de descarga
  download(id){
    this.loading = true;
    this._explorarServices.download(localStorage.getItem('token'), id, this.objDownload.id).subscribe(
        result => {
            this.loading = false;
            var url = window.URL.createObjectURL(result);
            window.open(url);
            this.regresarAExplorar();
        },
        error => {
            this.loading = false;
            var errorMessage = <any> error;
            console.log(errorMessage);
        }
    );
  }

  //Metodo de selección multiple (documentos y carpetas)
  setLstSelectedElements(){
    this.lstSelectedElements = [];
    for(let i = 0; i < this.dataTable.dataRows.length; i++){
      if(this.dataTable.dataRows[i][7]===true){
        this.lstSelectedElements.push(this.dataTable.dataRows[i]);
      }
    }
  }

  //Función para redirigir a la pantalla de descarga(descarga de múltiples elementos)
  toMultipleDownload(){
      this.lstDownload = [];
      let isOnlyFolder = false;
      let isOnlyDoc = false;
      let isDocFolder = false;
      for(let i = 0; i < this.lstSelectedElements.length; i++){
        if(this.lstSelectedElements[i][6]==='folder'){
          isOnlyFolder = true;
        }else{
          isOnlyFolder = false;
          break;
        }
      }
      if(isOnlyFolder===false){
        for(let i = 0; i < this.lstSelectedElements.length; i++){
          if(this.lstSelectedElements[i][6]==='document'){
            isOnlyDoc = true;
          }else{
            isOnlyDoc = false;
            break;
          }
        }
        if(isOnlyDoc===false){
          isDocFolder = true;
        }
      }
      if(isOnlyFolder){
        this.lstDownload = [{
          id:'content',
          value:'ZIP de contenido'
        },{
          id:'metadata',
          value:'Información como CSV'
        },{
          id:'contentMetadata',
          value:'Contenido e información'
        },{
          id:'list',
          value:'Lista'
        }]
      }else if(isOnlyDoc){
        this.lstDownload = [{
          id:'zipContent',
          value:'ZIP de contenido'
        },{
          id:'zipContentMetadata',
          value:'ZIP con propiedades'
        },{
          id:'list',
          value:'Lista'
        }]
      }else if(isDocFolder){
        this.lstDownload = [{
          id:'content',
          value:'ZIP de contenido'
        },{
          id:'metadata',
          value:'Información como CSV'
        },{
          id:'contentMetadata',
          value:'Contenido e información'
        },{
          id:'list',
          value:'Lista'
        }]
      }
      this.template = 'multipleDownload';
  }

  toMultipleSendEmail(){
    this.lstDownload = [];
      let isOnlyFolder = false;
      let isOnlyDoc = false;
      let isDocFolder = false;
      for(let i = 0; i < this.lstSelectedElements.length; i++){
        if(this.lstSelectedElements[i][6]==='folder'){
          isOnlyFolder = true;
        }else{
          isOnlyFolder = false;
          break;
        }
      }
      if(isOnlyFolder===false){
        for(let i = 0; i < this.lstSelectedElements.length; i++){
          if(this.lstSelectedElements[i][6]==='document'){
            isOnlyDoc = true;
          }else{
            isOnlyDoc = false;
            break;
          }
        }
        if(isOnlyDoc===false){
          isDocFolder = true;
        }
      }
      if(isOnlyFolder){
        this.lstDownload = [{
          id:'content',
          value:'ZIP de contenido'
        },{
          id:'metadata',
          value:'Información como CSV'
        },{
          id:'contentMetadata',
          value:'Contenido e información'
        },{
          id:'list',
          value:'Lista'
        }]
      }else if(isOnlyDoc){
        this.lstDownload = [{
          id:'zipContent',
          value:'ZIP de contenido'
        },{
          id:'zipContentMetadata',
          value:'ZIP con propiedades'
        },{
          id:'list',
          value:'Lista'
        }]
      }else if(isDocFolder){
        this.lstDownload = [{
          id:'content',
          value:'ZIP de contenido'
        },{
          id:'metadata',
          value:'Información como CSV'
        },{
          id:'contentMetadata',
          value:'Contenido e información'
        },{
          id:'list',
          value:'Lista'
        }]
      }
      this.template = 'multipleEmail';
  }

  /*/////////////////////////////////////////////////////////////////////////////////
              BLOQUE DE CONTEXT MENU
  /*/////////////////////////////////////////////////////////////////////////////////

  //Función genérica para convertir fecha de timestamp a date con formato específico
  convertDate(timestamp){
    let date = new Date(timestamp);
    let day = date.getDate();
    let month;
    if(date.getMonth()+1 < 10){
      month = '0' + (date.getMonth()+1).toString();
    }else{
      month = (date.getMonth()+1).toString();
    }
    let year = date.getFullYear();
    let hour = date.getHours();
    let min = date.getMinutes();
    return day + '/' + month + '/' + year + ' ' + hour + ':' + min;
  }     
       
}