package com.amit.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;  //Add
import com.badlogic.gdx.graphics.GL20; //Add
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont; //Add
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.lang.reflect.Array; //Add
import java.util.ArrayList;
import java.util.Random;

public class AndroidGame extends ApplicationAdapter {
	SpriteBatch batch;

	Texture background;
	Texture dizzy;
	Texture[] man; //Array[4]
	int manState =0;// For looping
	int pause =0;// For Pausing our character

	//Giving Gravity
	float gravity =0.2f;

	//Giving Velocity
	float velocity =0;
	//man y position
	int manY=0;

	Rectangle manRactangle;
	Random random;

	//Adding Score
	BitmapFont font;
	BitmapFont font1;

	int score =0;
	int gameState =0;

	Music music;

	//Coin
	ArrayList<Integer> coinXs =new ArrayList<Integer>();
	ArrayList<Integer> coinYs =new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangle =new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;

	//Bomb
	ArrayList<Integer> bombXs =new ArrayList<Integer>();
	ArrayList<Integer> bombYs =new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangle =new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background_dhussa.png");
		//For Sound
		music =Gdx.audio.newMusic(Gdx.files.internal("share_audio.mp3"));
		music.setLooping(true);
		music.setVolume(0.9f);

		man =new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

//man y position
		manY=Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		bomb =new Texture("bomb.png");
		dizzy =new Texture("dizzy-1.png");
		random = new Random();
		manRactangle= new Rectangle();
		font = new BitmapFont();
		font.setColor(Color.YELLOW);
		font.getData().setScale(10);

		font1 =new BitmapFont();
		font1.setColor(Color.BLUE);
		font1.getData().setScale(8);


	}


	public void makeCoin(){
		float height =random.nextFloat()*Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());

	}
	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState ==1){
			//Game is live
			//coins
			if (coinCount <100){
				coinCount++;
			}else{
				coinCount =0;
				makeCoin();
			}
			coinRectangle.clear();
			for (int i=0;i< coinXs.size(); i++){
				batch.draw(coin, coinXs.get(i),coinYs.get(i));

				coinXs.set(i,coinXs.get(i)-5);
				coinRectangle.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}


			///Bombs
			bombRectangle.clear();
			if (bombCount <250){
				bombCount++;
			}else{
				bombCount =0;
				makeBomb();
			}
			for (int i=0;i< bombXs.size(); i++){
				batch.draw(bomb, bombXs.get(i),bombYs.get(i));

				bombXs.set(i,bombXs.get(i)-7);
				bombRectangle.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}





			if(Gdx.input.justTouched()){
				velocity = -10;

			}



			if (pause <5){
				pause++;
			}else
			{ pause=0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}
//Calculate Man’s Velocity
			velocity =velocity + gravity ;
			manY -= velocity;

			if(manY<=0) {
				manY = 0;
			}

			if(manY>1390){
				manY =1390;
			}music.play();

		}else if (gameState == 0){
			//Waiting to Start
			font1.draw(batch,"  Tap Scree To Start", 0,Gdx.graphics.getWidth()-man[manState].getWidth());
			if (Gdx.input.justTouched()){
				gameState =1;
				music.play();
			}music.pause();
		}else if (gameState ==2){
			//Game Over
			font1.draw(batch,"You Lose! Again Tap",0 ,Gdx.graphics.getWidth()-man[manState].getWidth());
			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY =Gdx.graphics.getHeight() / 2;
				score =0;
				velocity =0;
				coinXs.clear();
				coinYs.clear();
				coinRectangle.clear();
				coinCount =0;

				bombXs.clear();
				bombYs.clear();
				bombRectangle.clear();
				bombCount =0;
			}music.pause();
		}




		if (gameState ==2) {
			batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}

		manRactangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY,man[manState].getWidth(),man[manState].getHeight());

		for(int i=0; i<coinRectangle.size();i++){
			if (Intersector.overlaps(manRactangle,coinRectangle.get(i))){
				Gdx.app.log("Coin!","Collison!");
				score++;
				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;

			}
		}


		for(int i=0; i<bombRectangle.size();i++){
			if (Intersector.overlaps(manRactangle,bombRectangle.get(i))){
				Gdx.app.log("Bomb!","Collison!");
				gameState =2;
			}
		}


		font.draw(batch,String.valueOf(score),80,200);

		batch.end();



	}

	@Override
	public void dispose () {
		batch.dispose();

	}

}
