import { Routes } from '@angular/router';

import { DatabaseComponent } from './database/database.component';

export const DatabaseRoutes: Routes = [

    {
        path: '',
        children: [ {
            path: '',
            component: DatabaseComponent
        } ]
    }
];
