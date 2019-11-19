import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from "@angular/router";
import { TranslateModule } from "@ngx-translate/core";
import { GuessNameComponent } from "./guess-name.component";
import { GuessPictureComponent } from "./guess-picture.component";
import { GuessTalkComponent } from "./guess-talk.component";
import { GuessSpeakerComponent } from "./guess-speaker.component";
import { CancelGameComponent } from "./cancel-game.component";
import { GeneralModule } from "../general/general.module";
import { MessageModule } from "../message/message.module";

@NgModule({
  declarations: [
    CancelGameComponent,
    GuessNameComponent,
    GuessPictureComponent,
    GuessTalkComponent,
    GuessSpeakerComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule,
    GeneralModule,
    MessageModule
  ]
})
export class GuessModule {
}
