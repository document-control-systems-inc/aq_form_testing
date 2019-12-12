import { Routes } from '@angular/router';

import { ProcessComponent } from './process/process.component';

export const ProcessRoutes: Routes = [
    {
        path: '',
        children: [ {
            path: '',
            component: ProcessComponent
        } ]
    }
];
