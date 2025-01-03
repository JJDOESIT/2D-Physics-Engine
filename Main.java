package it.jjdoes.PhysicsEngine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import it.jjdoes.PhysicsEngine.SoftBody.GodCode;

public class Main implements ApplicationListener {
    float screenWidth;
    float screenHeight;
    float headerHeight;
    FitViewport viewport;
    ShapeRenderer shapeRenderer;
    PolygonSpriteBatch polygonSpriteBatch;
    SpriteBatch spriteBatch;
    Stage stage;
    GodCode gc;

    @Override
    public void create() {
        this.screenWidth = 1920;
        this.screenHeight = 1080;
        this.headerHeight = 200;
        this.viewport = new FitViewport(this.screenWidth, this.screenHeight);
        this.shapeRenderer = new ShapeRenderer();
        this.polygonSpriteBatch = new PolygonSpriteBatch();
        this.spriteBatch = new SpriteBatch();
        this.stage = new Stage(this.viewport, this.polygonSpriteBatch);
        Header.initializeHeader(this.screenWidth, this.screenHeight, this.headerHeight, this.stage);
        World.initialize(this.screenWidth, this.screenHeight, this.headerHeight, true);
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    @Override
    public void render() {
        //printFps();
        input();
        logic();
        draw();
    }

    private void printFps(){
        System.out.println((int) (1 / Gdx.graphics.getDeltaTime()));
    }

    private void input(){
        World.handleInput(this.viewport);
    }

    private void logic() {
        World.logic(Gdx.graphics.getDeltaTime(), 1);
    }

    private void draw(){
        World.draw(this.viewport, this.shapeRenderer, this.polygonSpriteBatch);
        World.quadtree.drawQuadTree(this.viewport, this.shapeRenderer, this.spriteBatch, false);
        this.stage.draw();
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
