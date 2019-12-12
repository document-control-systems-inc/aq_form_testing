import { Component, OnInit, ElementRef } from '@angular/core';

@Component({
    selector: 'app-footer-cmp',
    templateUrl: 'footer.component.html'
})

export class FooterComponent {
    test: Date = new Date();
    languages = [
      {value: 'es', language: 'Español'},
      {value: 'en', language: 'English'}
    ];
}
