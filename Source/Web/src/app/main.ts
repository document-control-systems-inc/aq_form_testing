import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app.module';
import { enableProdMode } from '@angular/core';
import 'eonasdan-bootstrap-datetimepicker';

enableProdMode();
platformBrowserDynamic().bootstrapModule(AppModule);
