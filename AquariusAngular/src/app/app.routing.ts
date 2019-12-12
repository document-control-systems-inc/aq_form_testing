import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

//Importar componentes
import { LoginComponent } from './components/unicos/login/login.component';
import { PortalComponent } from './components/unicos/portal/portal.component';
import { DocumentViewerComponent } from './components/unicos/documentViewer/documentViewer.component';

const appRoutes: Routes = [
	{path: '', component: LoginComponent},
	{path: 'portal', component: PortalComponent},
	{path: 'documentViewer', component: DocumentViewerComponent},
	{path: '**', component: LoginComponent}
];

export const appRoutingProviders: any[] = [];

export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);