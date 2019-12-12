import { Routes } from '@angular/router';

import { DocClassComponent } from './docclass/docclass.component';

export const DocClassRoutes: Routes = [
    {
        path: '',
        children: [ {
            path: '',
            component: DocClassComponent
        } ]
    }
];
