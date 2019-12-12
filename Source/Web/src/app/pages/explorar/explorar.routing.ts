import { Routes } from '@angular/router';

import { DocumentViewerComponent } from './documentViewer/documentViewer.component';
import { ExplorarComponent } from './explorar/explorar.component';

export const ExplorarRoutes: Routes = [

    {
        path: '',
        children: [ {
            path: '',
            component: ExplorarComponent
        }, {
            path: 'view',
            component: DocumentViewerComponent
        }]
    }
];
