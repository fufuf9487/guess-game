import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { TranslateService } from "@ngx-translate/core";
import { Result } from "../../shared/models/result.model";
import { State } from "../../shared/models/state.model";
import { GuessMode } from "../../shared/models/guess-type.model";
import { AnswerService } from "../../shared/services/answer.service";
import { StateService } from "../../shared/services/state.service";

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html'
})
export class ResultComponent implements OnInit {
  public speakersImageDirectory: string = 'assets/images/speakers';
  public result = new Result();
  public isQuestionPicture = true;

  constructor(private answerService: AnswerService, private stateService: StateService, private router: Router,
              public translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.loadResult();
  }

  loadResult() {
    this.answerService.getResult()
      .subscribe(data => {
        this.result = data;
        this.isQuestionPicture = (GuessMode.GuessNameByPhotoMode === this.result.guessMode) || (GuessMode.GuessTalkBySpeakerMode === this.result.guessMode);
      })
  }

  restart() {
    this.stateService.setState(State.StartState)
      .subscribe(data => {
          this.router.navigateByUrl('/start');
        }
      );
  }

  isSkippedVisible() {
    return this.result.skippedAnswers > 0;
  }

  isSpeakerErrorDetailsListVisible() {
    return this.result.speakerErrorDetailsList && (this.result.speakerErrorDetailsList.length > 0);
  }

  isTalkErrorDetailsListVisible() {
    return this.result.talkErrorDetailsList && (this.result.talkErrorDetailsList.length > 0);
  }

  isErrorDetailsListVisible() {
    return this.isSpeakerErrorDetailsListVisible() || this.isTalkErrorDetailsListVisible();
  }
}
