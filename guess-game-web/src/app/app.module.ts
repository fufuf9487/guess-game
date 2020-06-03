import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClient, HttpClientModule } from "@angular/common/http";
import { registerLocaleData } from '@angular/common';
import localeRu from '@angular/common/locales/ru';
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import { TranslateLoader, TranslateModule } from "@ngx-translate/core";

import { AppComponent } from './app.component';
import { StartModule } from "./modules/start/start.module";
import { GuessModule } from "./modules/guess/guess.module";
import { ResultModule } from "./modules/result/result.module";
import { RouterModule, Routes } from "@angular/router";
import { StartComponent } from "./modules/start/start.component";
import { ResultComponent } from "./modules/result/result.component";
import { GuessNameByPhotoComponent } from "./modules/guess/guess-name-by-photo.component";
import { GuessPhotoByNameComponent } from "./modules/guess/guess-photo-by-name.component";
import { GuessTalkBySpeakerComponent } from "./modules/guess/guess-talk-by-speaker.component";
import { GuessSpeakerByTalkComponent } from "./modules/guess/guess-speaker-by-talk.component";
import { GuessAccountBySpeakerComponent } from './modules/guess/guess-account-by-speaker.component';
import { GuessSpeakerByAccountComponent } from './modules/guess/guess-speaker-by-account.component';
import { UnknownModule } from "./modules/unknown/unknown.module";
import { NotFoundComponent } from "./modules/unknown/not-found.component";
import { MessageModule } from "./modules/message/message.module";
import { AnswerService } from "./shared/services/answer.service";
import { QuestionService } from "./shared/services/question.service";
import { StateService } from "./shared/services/state.service";
import { StateGuard } from "./shared/guards/state.guard";
import { CancelGameComponent } from "./modules/guess/cancel-game.component";

const routes: Routes = [
  {path: 'start', component: StartComponent, canActivate: [StateGuard]},
  {path: 'guess/name-by-photo', component: GuessNameByPhotoComponent, canActivate: [StateGuard]},
  {path: 'guess/photo-by-name', component: GuessPhotoByNameComponent, canActivate: [StateGuard]},
  {path: 'guess/talk-by-speaker', component: GuessTalkBySpeakerComponent, canActivate: [StateGuard]},
  {path: 'guess/speaker-by-talk', component: GuessSpeakerByTalkComponent, canActivate: [StateGuard]},
  {path: 'guess/account-by-speaker', component: GuessAccountBySpeakerComponent, canActivate: [StateGuard]},
  {path: 'guess/speaker-by-account', component: GuessSpeakerByAccountComponent, canActivate: [StateGuard]},
  {path: 'result', component: ResultComponent, canActivate: [StateGuard]},
  {path: 'cancel', component: CancelGameComponent},
  {path: '', pathMatch: 'full', redirectTo: 'start'},
  {path: "**", component: NotFoundComponent}
];

// AoT requires an exported function for factories
export function HttpLoaderFactory(httpClient: HttpClient) {
  return new TranslateHttpLoader(httpClient);
}

registerLocaleData(localeRu, 'ru');

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    GuessModule,
    MessageModule,
    ResultModule,
    StartModule,
    UnknownModule
  ],
  providers: [AnswerService, QuestionService, StateService, StateGuard],
  bootstrap: [AppComponent]
})
export class AppModule {
}
