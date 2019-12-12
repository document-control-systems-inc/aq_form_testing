import { Routes } from '@angular/router';

import { RegisterComponent } from './register/register.component';
import { PricingComponent } from './pricing/pricing.component';
import { LockComponent } from './lock/lock.component';
import { LoginComponent } from './login/login.component';
import { PortalComponent } from './portal/portal.component';

export const PagesRoutes: Routes = [

    {
        path: '',
        children: [ {
            path: '',
            component: LoginComponent
        }, {
            path: 'portal',
            component: PortalComponent
        }, {
            path: 'register',
            component: RegisterComponent
        }, {
            path: 'pricing',
            component: PricingComponent
        }]
    }
];
