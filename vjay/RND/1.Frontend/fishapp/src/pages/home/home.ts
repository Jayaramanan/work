import { Component, OnInit } from '@angular/core';
import { NavController } from 'ionic-angular';
import { CartpagePage } from '../cartpage/cartpage';
import { PeopleServiceProvider } from '../../providers/people-service/people-service';
import { Fish } from '../../providers/people-service/fish.model';
import { AngularFireList, AngularFireDatabase } from 'angularfire2/database';
import { Observable } from 'rxjs';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage implements OnInit {
  public fishes: Fish[]; 
  
  public itemsRef : AngularFireList<any>;
  constructor(public navCtrl: NavController,public peopleService : PeopleServiceProvider,public db : AngularFireDatabase) {

  }
  ngOnInit(){
    this.itemsRef = this.db.list('/fishcoll/edible');
    this.itemsRef.snapshotChanges()
    .subscribe(actions => {
       actions.forEach(action => {
        console.log(action.type);
        console.log(action.key);
        console.log(action.payload.val());
    });
  });
  }

loadFishes(){
 // this.fishes = this.peopleSerive.load()

}

gotoCheckout(){
  this.navCtrl.push(CartpagePage);
}
}
