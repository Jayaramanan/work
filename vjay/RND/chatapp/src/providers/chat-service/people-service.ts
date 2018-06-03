//import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import 'rxjs/add/operator/map';
import {  AngularFireDatabase, AngularFireList } from 'angularfire2/database';

/*
  Generated class for the PeopleServiceProvider provider.

  See https://angular.io/guide/dependency-injection for more info on providers
  and Angular DI.
*/
@Injectable()
export class PeopleServiceProvider {
  private baseUrl = '/fishcoll/edible';
  
  fishData :AngularFireList<any>;
  constructor(private firebaseDb: AngularFireDatabase) {
    console.log('Hello PeopleServiceProvider Provider');
    //this.load();
  }

  load(){
    return this.fishData = this.firebaseDb.list(this.baseUrl);
  }
}

