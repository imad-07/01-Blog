import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from "../navbar/navbar";

@Component({
  selector: 'app-home',
  imports: [RouterOutlet, Navbar],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {
}
