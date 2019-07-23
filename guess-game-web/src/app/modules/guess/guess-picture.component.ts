import { Component } from '@angular/core';
import { NamePictures } from "../../shared/models/name-pictures.model";
import { StateService } from "../../shared/services/state.service";
import { AnswerService } from "../../shared/services/answer.service";
import { Router } from "@angular/router";
import { State } from "../../shared/models/state.model";

@Component({
  selector: 'app-guess-picture',
  templateUrl: './guess-picture.component.html'
})
export class GuessPictureComponent {
  private imageDirectory: string = 'assets/images';
  private namePictures: NamePictures = new NamePictures();
  private title: string;
  private imageSource0: string;
  private imageSource1: string;
  private imageSource2: string;
  private imageSource3: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getNamePictures()
      .subscribe(data => {
          if (data) {
            this.namePictures = data;
            this.title = `${this.namePictures.questionSetName} (${this.namePictures.currentIndex + 1}/${this.namePictures.totalNumber})`;
            this.imageSource0 = `${this.imageDirectory}/${this.namePictures.fileName0}`;
            this.imageSource1 = `${this.imageDirectory}/${this.namePictures.fileName1}`;
            this.imageSource2 = `${this.imageDirectory}/${this.namePictures.fileName2}`;
            this.imageSource3 = `${this.imageDirectory}/${this.namePictures.fileName3}`;
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.namePictures.currentIndex, id)
      .subscribe(data => {
          this.loadQuestion();
        }
      );
  }

  result() {
    this.stateService.setState(State.ResultState)
      .subscribe(date => {
          this.router.navigateByUrl('/result');
        }
      );
  }

  cancel() {
    this.router.navigateByUrl('/cancel');
  }
}
