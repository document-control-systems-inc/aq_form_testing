import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { LbdTableComponent } from '../lbd/lbd-table/lbd-table.component';

import { ChartsComponent } from './charts.component';
import { ChartsRoutes } from './charts.routing';

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(ChartsRoutes),
        FormsModule
    ],
    declarations: [ChartsComponent],
    schemas: [ NO_ERRORS_SCHEMA ]
})

export class ChartsModule {}
