import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { TableModule } from 'primeng/table';
import { GeneralModule } from '../../general/general.module';
import { InformationModule } from '../information.module';
import { MessageModule } from '../../message/message.module';
import { SpeakersListComponent } from './speakers-list.component';
import { SpeakersSearchComponent } from './speakers-search.component';
import { SpeakersSwitcherComponent } from './speakers-switcher.component';

@NgModule({
  declarations: [
    SpeakersListComponent,
    SpeakersSearchComponent,
    SpeakersSwitcherComponent],
  imports: [
    CommonModule,
    RouterModule,
    TableModule,
    GeneralModule,
    InformationModule,
    MessageModule,
    TranslateModule
  ]
})
export class SpeakersModule {
}
