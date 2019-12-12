import { Routes } from '@angular/router';

import { AdminComponent } from './admin/admin.component';

export const AdminRoutes: Routes = [
    {
        path: '',
        children: [ {
            path: '',
            component: AdminComponent
        } ]
    }
];
