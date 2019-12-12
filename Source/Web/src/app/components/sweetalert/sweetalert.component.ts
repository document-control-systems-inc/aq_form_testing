import { Component } from '@angular/core';

declare var $: any;
declare var swal: any;

@Component({
    selector: 'app-sweetalert-cmp',
    templateUrl: 'sweetalert.component.html'
})

export class SweetAlertComponent {
    public user = "ecm";
    showSwal(type) {
        if (type === 'basic') {
            swal({
                type: 'error',
                title: 'Here is a message!',
                html: '<div class="form-group">' +
                          '<p><strong>Error 1.</strong> Blablabla</p>' +
                      '</div>',
                buttonsStyling: false,
                confirmButtonClass: 'btn btn-success'
            });
        } else if (type === 'title-and-text') {
            swal({
                title: 'Here is a message!',
                text: 'It is pretty, is not it?',
                buttonsStyling: false,
                confirmButtonClass: 'btn btn-info'
            });

        } else if (type === 'success-message') {
            swal({
                type: 'success',
                title: 'Good job!',
                text: 'You clicked the button!',
                buttonsStyling: false,
                confirmButtonClass: 'btn btn-success'

            });

        } else if (type === 'warning-message-and-confirmation') {
            swal({
                    title: 'Are you sure?',
                    text: 'You will not be able to revert this!',
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonClass: 'btn btn-success',
                    cancelButtonClass: 'btn btn-danger',
                    confirmButtonText: 'Yes, delete it!',
                    buttonsStyling: false
                }).then(function() {
                  swal({
                    title: 'Deleted!',
                    text: 'Your file has been deleted.',
                    type: 'success',
                    confirmButtonClass: 'btn btn-success',
                    buttonsStyling: false
                });
                });
            } else if (type === 'warning-message-and-cancel') {
            swal({
                    title: 'Are you sure?',
                    text: 'You will not be able to recover this imaginary file!',
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Yes, delete it!',
                    cancelButtonText: 'No, keep it',
                    confirmButtonClass: 'btn btn-success',
                    cancelButtonClass: 'btn btn-danger',
                    buttonsStyling: false
                }).then(function() {
                  swal({
                    title: 'Deleted!',
                    text: 'Your imaginary file has been deleted.',
                    type: 'success',
                    confirmButtonClass: 'btn btn-success',
                    buttonsStyling: false
                });
                }, function(dismiss) {
                  // dismiss can be 'overlay', 'cancel', 'close', 'esc', 'timer'
                  if (dismiss === 'cancel') {
                    swal({
                      title: 'Cancelled',
                      text: 'Your imaginary file is safe :)',
                      type: 'error',
                      confirmButtonClass: 'btn btn-info',
                      buttonsStyling: false
                  });
                  }
              });
          } else if (type === 'custom-html') {
            swal({
                title: 'HTML example',
                buttonsStyling: false,
                confirmButtonClass: 'btn btn-success',
                html:
                        'You can use <b>bold text</b>, ' +
                        '<a href="http://github.com">links</a> ' +
                        'and other HTML tags'
                });

            } else if (type === 'auto-close') {
            swal({ title: 'Auto close alert!',
            text: 'I will close in 2 seconds.',
                   timer: 2000,
                   showConfirmButton: false
                });
            } else if (type === 'input-field') {


                swal({
                    title: 'Sesión expirada',
                    html: '<div class="form-group">' +
                              '<p>Favor de volver a iniciar sesión.</p>' +
                              '<div class="card-content">' +
                                    '<div class="input-group">' +
                                        '<span class="input-group-addon">' +
                                            '<i class="material-icons">face</i>' +
                                        '</span>' +
                                        '<div class="form-group label-floating">' +
                                            '<label class="control-label">User name</label>' + 
                                            '<input type="text" class="form-control" value="'+this.user+'" disabled>' +
                                        '</div>' +
                                    '</div>' +
                                    '<div class="input-group">' +
                                        '<span class="input-group-addon">' + 
                                            '<i class="material-icons">lock_outline</i>' +
                                        '</span>' + 
                                        '<div class="form-group label-floating">' +
                                            '<label class="control-label">Password</label>' + 
                                            '<input type="password" class="form-control" [(ngModel)]="password" name="password">' +
                                        '</div>' +
                                    '</div>' +
                                '</div>' +
                          '</div>' +
                          '<div class="footer text-center">' + 
                              '<button type="submit" class="btn btn-rose btn-simple btn-wd btn-lg" (click)="login()">Enter!</button>' +
                              '<button type="submit" class="btn btn-info btn-simple btn-wd btn-lg" (click)="login()">Cerrar sesión</button>' +
                          '</div>',
                    type: 'error',
                    showCancelButton: false,
                    showConfirmButton: false,
                    allowOutsideClick: false,
                    allowEscapeKey: false
                });


        }
    }
}
