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
            path: 'explorar',
            loadChildren: './pages/explorar/explorar.module#ExplorarModule'
        }, {
            path: 'busqueda',
            loadChildren: './pages/busqueda/busqueda.module#BusquedaModule'
        }, {
            path: 'tareas',
            loadChildren: './pages/tareas/tareas.module#TareasModule'
        }, {
            path: 'captura',
            loadChildren: './pages/captura/captura.module#CapturaModule'
        }, {
            path: 'charts',
            loadChildren: './charts/charts.module#ChartsModule'
        }, {
            path: 'calendar',
            loadChildren: './calendar/calendar.module#CalendarModule'
        }, {
            path: '',
            loadChildren: './userpage/user.module#UserModule'
        }, {
            path: '',
            loadChildren: './timeline/timeline.module#TimelineModule'
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