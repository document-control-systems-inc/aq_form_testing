import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { routing, appRoutingProviders } from './app.routing';
import { HttpModule } from '@angular/http';
import { HttpClientModule, HttpClient } from '@angular/common/http';

//Internacionalización
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

//Componentes unicos
import { AppComponent } from './app.component';
import { LoginComponent } from './components/unicos/login/login.component';
import { PortalComponent } from './components/unicos/portal/portal.component';
import { DocumentViewerComponent } from './components/unicos/documentViewer/documentViewer.component';

//Componentes recurrentes
import { PdfComponent } from './components/recurrentes/pdfViewer/pdfViewer.component';
import { PdfEditorComponent } from './components/recurrentes/pdfEditor/pdfEditor.component';
import { FooterComponent } from './components/recurrentes/footer/footer.component';
import { SystemPropertiesComponent } from './components/recurrentes/systemProperties/systemProperties.component';
import { DocumentPropertiesComponent } from './components/recurrentes/documentProperties/documentProperties.component';
import { VersionHistoryComponent } from './components/recurrentes/versionHistory/versionHistory.component';
import { CommentsComponent } from './components/recurrentes/comments/comments.component';
import { ReactsComponent } from './components/recurrentes/reacts/reacts.component';
import { NavbarComponent } from './components/recurrentes/navbar/navbar.component';
import { LoginServices } from './components/unicos/login/login.services';

//Pipes
import { SafePipe } from './pipes/SafePipe';

//Uso de librerías ajenas
import { PdfViewerComponent } from 'ng2-pdf-viewer';

//Uso de entities
import { Login } from './entity/login';
import { DocumentViewer } from './entity/documentViewer'; 

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http);
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent, 
    NavbarComponent,
    PortalComponent,
    DocumentViewerComponent,
    SafePipe,
    PdfViewerComponent,
    PdfComponent,
    PdfEditorComponent,
    FooterComponent,
    SystemPropertiesComponent,
    DocumentPropertiesComponent,
    VersionHistoryComponent,
    CommentsComponent,
    ReactsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    routing, 
    HttpModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
          provide: TranslateLoader,
          useFactory: HttpLoaderFactory,
          deps: [HttpClient]
      }
    })
  ],
  providers: [appRoutingProviders, Login, LoginServices, DocumentViewer],
  bootstrap: [AppComponent]
})
export class AppModule { 

}
