import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { PhotoNames } from "../../shared/models/photo-names.model";
import { StateService } from "../../shared/services/state.service";
import { AnswerService } from "../../shared/services/answer.service";
import { State } from "../../shared/models/state.model";

@Component({
  selector: 'app-guess-name',
  templateUrl: './guess-name-by-photo.component.html'
})
export class GuessNameByPhotoComponent implements OnInit {
  private imageDirectory: string = 'assets/images';
  private eventsImageDirectory: string = `${this.imageDirectory}/events`;
  private speakersImageDirectory: string = `${this.imageDirectory}/speakers`;
  public photoNames: PhotoNames = new PhotoNames();
  public title: string;
  public logoImageSource: string;
  public imageSource: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getPhotoNames()
      .subscribe(data => {
          if (data) {
            this.photoNames = data;
            this.title = `${this.photoNames.questionSetName} (${this.photoNames.currentIndex + 1}/${this.photoNames.totalNumber})`;
            this.imageSource = `${this.speakersImageDirectory}/${this.photoNames.fileName}`;

            if (this.photoNames.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.photoNames.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.photoNames.currentIndex, id)
      .subscribe(data => {
          this.loadQuestion();
        }
      );
  }

  result() {
    this.stateService.setState(State.ResultState)
      .subscribe(data => {
          this.router.navigateByUrl('/result');
        }
      );
  }

  cancel() {
    this.router.navigateByUrl('/cancel');
  }
}
