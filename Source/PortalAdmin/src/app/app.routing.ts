import { Routes } from '@angular/router';

import { AdminLayoutComponent } from './layouts/admin/admin-layout.component';
import { AuthLayoutComponent } from './layouts/auth/auth-layout.component';

export const AppRoutes: Routes = [
    {
      path: '',
      redirectTo: 'login',
      pathMatch: 'full',
    }, {
      path: '',
      component: AdminLayoutComponent,
      children: [
        {
            path: '',
            loadChildren: './pages/pages.module#PagesModule'
        },
        {
            path: 'database',
            loadChildren: './pages/database/database.module#DatabaseModule'
        },
        {
            path: 'config',
            loadChildren: './pages/configuration/configuration.module#ConfigurationModule'
        },
        {
            path: 'class',
            loadChildren: './pages/class/docclass.module#DocClassModule'
        },
        {
            path: 'users',
            loadChildren: './pages/users/users.module#UsersModule'
        },
        {
            path: 'admins',
            loadChildren: './pages/admin/admin.module#AdminModule'
        },
        {
            path: 'groups',
            loadChildren: './pages/groups/groups.module#GroupsModule'
        },
        {
            path: 'process',
            loadChildren: './pages/process/process.module#ProcessModule'
        },
        {
            path: 'permisos',
            loadChildren: './pages/permisos/permisos.module#PermisosModule'
        }
      ]}, 
      {
        path: '',
        component: AuthLayoutComponent,
        children: [{
          path: 'login',
          loadChildren: './pages/pages.module#PagesModule'
        }]
      }
];