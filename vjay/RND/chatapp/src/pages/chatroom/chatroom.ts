import { Component, OnInit } from '@angular/core';
import { IonicPage, NavController, NavParams } from 'ionic-angular';
import { ChatObj } from '../../providers/chat-service/chatObj.model';
import { ChatServiceProvider } from '../../providers/chat-service/chat-service';
import { AuthService } from '../../providers/auth-service/auth-service';
import { PeopleServiceProvider } from '../../providers/chat-service/people-service';

/**
 * Generated class for the ChatroomPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-chatroom',
  templateUrl: 'chatroom.html',
})
export class ChatroomPage  implements OnInit {
  public chatMsg : string;
  public chatHistory : ChatObj[]
  constructor(public navCtrl: NavController, public navParams: NavParams,
              public chatservice : ChatServiceProvider,public authService : AuthService,
              public peopleService : PeopleServiceProvider) {
    this.chatHistory = [];
  }

  ngOnInit(){
    console.log("load");
/*     return this.chatservice.load()
        .snapshotChanges().subscribe(items=>{
            console.log(items);
        }); */
      console.log(this.peopleService.load().snapshotChanges().subscribe());
  } 

  ionViewDidLoad() {
    console.log('ionViewDidLoad ChatroomPage');
  }

  chat(){
    //alert("sadas");
    if(this.chatMsg.length!=0 && this.chatMsg!='undefined'){
      this.addChatObj();
      this.chatMsg = "";
    }
  }

  addChatObj(){
    let chatObj = new ChatObj();
    chatObj.author = this.authService.currentUser.displayName
    chatObj.authorEmail = this.authService.currentUser.email
    chatObj.msg = this.chatMsg;
    chatObj.media = null;  
    this.chatservice.insertChat(chatObj);
  }


}
