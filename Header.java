package it.jjdoes.PhysicsEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class Header {
    // Initialize the header
    public static void initializeHeader(float screenWidth, float screenHeight, float headerHeight, Stage stage){
        // Create the table
        Table table = new Table();
        table.setSize(screenWidth, headerHeight);
        table.setPosition(0, screenHeight - headerHeight);
        table.top().left();

        // Default font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("queensides.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 35;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels

        // Add a triangle option
        addShapeToHeader(table, font, "Triangle", 300, 100, false);
        // Add a square option
        addShapeToHeader(table, font, "Square", 300, 100, true);
        // Add a pentagon option
        addShapeToHeader(table, font, "Pentagon",  300, 100, false);
        // Add the gravity slider
        addGravitySliderToHeader(table, font, 330, 100);
        // Add mass label
        addLabelToHeader(table, font, "Mass", 150, 100, new Color(102 / 255f, 51 / 255f, 0 / 255f, 1), new Color(255 / 255f, 255 / 255f, 255 / 255f, 1));
        // Add length label
        addLabelToHeader(table, font, "Length", 150, 100, new Color(102 / 255f, 51 / 255f, 0 / 255f, 1), new Color(255 / 255f, 255 / 255f, 255 / 255f, 1));
        // Add rotation label
        addLabelToHeader(table, font, "Rotation", 150, 100, new Color(102 / 255f, 51 / 255f, 0 / 255f, 1), new Color(255 / 255f, 255 / 255f, 255 / 255f, 1));
        // Add static button
        addStaticButtonToHeader(table, font, "Static", 240, 100);
        // Next row
        table.row();
        // Add a hexagon option
        addShapeToHeader(table, font, "Hexagon",  300, 100, false);
        // Add an octagon option
        addShapeToHeader(table, font, "Octagon",  300, 100, false);
        // Add a circle option
        addShapeToHeader(table, font, "Circle",  300, 100, false);
        // Add gravity label
        addLabelToHeader(table, font, "Gravity Slider", 330, 100, new Color(204 / 255f, 229 / 255f, 255 / 255f, 1), new Color(0 / 255f, 0 / 255f, 102 / 255f, 1));
        // Add mass input
        addMassInputToHeader(table, font, "5", 150, 100);
        // Add side length input
        addSideLengthInputToHeader(table, font, "20", 150, 100);
        // Add rotation input
        addRotationInputToHeader(table, font, "45", 150, 100);
        // Add swap mode button
        addSwapModeButtonToHeader(table, font, "Soft Body", 240, 100);
        stage.addActor(table);
    }

    // Add an arbitrary shape button to the header
    public static void addShapeToHeader(Table table, BitmapFont font, String externalName, float width, float height, boolean selected){
        // Create the skin
        Skin skin = new Skin();
        Pixmap notSelectedPM = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        notSelectedPM.setColor(255 / 255f, 102 / 255f, 102 / 255f, 1);
        notSelectedPM.fill();
        Pixmap selectedPM = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        selectedPM.setColor(0 / 255f, 204 / 255f, 0 / 255f, 1);
        selectedPM.fill();
        // Add two skins (selected or not selected)
        skin.add("not-selected",  new TextureRegion(new Texture(notSelectedPM)));
        skin.add("selected",  new TextureRegion(new Texture(selectedPM)));
        skin.add("default", font);

        // Create the not selected style
        TextButton.TextButtonStyle notSelectedStyle = new TextButton.TextButtonStyle();
        notSelectedStyle.up = skin.getDrawable("not-selected");
        notSelectedStyle.font = skin.getFont("default");

        // Create the selected style
        TextButton.TextButtonStyle selectedStyle = new TextButton.TextButtonStyle();
        selectedStyle.up = skin.getDrawable("selected");
        selectedStyle.font = skin.getFont("default");

        // Create the button
        skin.add("default", notSelectedStyle);
        TextButton button = new TextButton(externalName, skin, "default");

        // If the button is selected, set the selected shape
        if (selected){
            World.setSelectedShape(button);
        }

        // Add an event handler to the button
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // De-select the other previously clicked button
                World.getSelectedShape().setStyle(notSelectedStyle);
                // Select the current shape
                World.setSelectedShape(button);
                button.setStyle(selectedStyle);
            }
        });
        // Set the button to be either selected by default or not
        button.setChecked(selected);
        // Add the button to the table
        table.add(button).width(width).height(height);
    }

    // Add the static button to the header
    public static void addStaticButtonToHeader(Table table, BitmapFont font, String externalName, float width, float height){
        // Create the skin
        Skin skin = new Skin();
        Pixmap notSelectedPM = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        notSelectedPM.setColor(255 / 255f, 102 / 255f, 102 / 255f, 1);
        notSelectedPM.fill();
        Pixmap selectedPM = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        selectedPM.setColor(0 / 255f, 204 / 255f, 0 / 255f, 1);
        selectedPM.fill();
        // Add two skins (selected or not selected)
        skin.add("not-selected",  new TextureRegion(new Texture(notSelectedPM)));
        skin.add("selected",  new TextureRegion(new Texture(selectedPM)));
        skin.add("default", font);

        // Create the not selected style
        TextButton.TextButtonStyle notSelectedStyle = new TextButton.TextButtonStyle();
        notSelectedStyle.up = skin.getDrawable("not-selected");
        notSelectedStyle.font = skin.getFont("default");

        // Create the selected style
        TextButton.TextButtonStyle selectedStyle = new TextButton.TextButtonStyle();
        selectedStyle.up = skin.getDrawable("selected");
        selectedStyle.font = skin.getFont("default");

        // Create the button
        skin.add("default", notSelectedStyle);
        TextButton button = new TextButton(externalName, skin, "default");

        // If the button is selected, set the selected shape
        World.setSelectedStaticOption(true);

        // Add an event handler to the button
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // If the static option is selected
                if (button.isChecked()){
                    // Set the style and set movable to false
                    button.setStyle(selectedStyle);
                    World.setSelectedStaticOption(false);
                }
                // If the static option is NOT selected
                else{
                    // Set the style and set moveable to true
                    button.setStyle(notSelectedStyle);
                    World.setSelectedStaticOption(true);
                }
            }
        });
        // Add the button to the table
        table.add(button).width(width).height(height);
    }

    // Add the static button to the header
    public static void addSwapModeButtonToHeader(Table table, BitmapFont font, String externalName, float width, float height){
        // Create the skin
        Skin skin = new Skin();
        Pixmap notSelectedPM = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        notSelectedPM.setColor(255 / 255f, 102 / 255f, 102 / 255f, 1);
        notSelectedPM.fill();
        Pixmap selectedPM = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        selectedPM.setColor(0 / 255f, 204 / 255f, 0 / 255f, 1);
        selectedPM.fill();
        // Add two skins (selected or not selected)
        skin.add("not-selected",  new TextureRegion(new Texture(notSelectedPM)));
        skin.add("selected",  new TextureRegion(new Texture(selectedPM)));
        skin.add("default", font);

        // Create the not selected style
        TextButton.TextButtonStyle notSelectedStyle = new TextButton.TextButtonStyle();
        notSelectedStyle.up = skin.getDrawable("not-selected");
        notSelectedStyle.font = skin.getFont("default");

        // Create the selected style
        TextButton.TextButtonStyle selectedStyle = new TextButton.TextButtonStyle();
        selectedStyle.up = skin.getDrawable("selected");
        selectedStyle.font = skin.getFont("default");

        // Create the button
        skin.add("default", notSelectedStyle);
        TextButton button = new TextButton(externalName, skin, "default");

        // Add an event handler to the button
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // If the static option is selected
                if (button.isChecked()){
                    // Set the style and set movable to false
                    button.setStyle(selectedStyle);
                    World.swapMode();
                }
                // If the static option is NOT selected
                else{
                    // Set the style and set moveable to true
                    button.setStyle(notSelectedStyle);
                    World.swapMode();
                }
            }
        });
        // Add the button to the table
        table.add(button).width(width).height(height);
    }

    // Add an arbitrary label to the header
    public static void addLabelToHeader(Table table, BitmapFont font, String text, float width, float height, Color bgColor, Color fontColor){
        // Create the skin
        Skin skin = new Skin();

        // Create the pixmap
        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bg.setColor(bgColor);
        bg.fill();
        skin.add("bg", new TextureRegion(new Texture(bg)));
        skin.add("default", font);

        // Create the label style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = fontColor;
        labelStyle.background = skin.getDrawable("bg");
        skin.add("default", labelStyle);

        // Create the label
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);
        // Add the label to the tabel
        table.add(label).width(width).height(height);
    }

    // Add the mass input field to the header
    public static void addMassInputToHeader(Table table, BitmapFont font, String text, float width, float height){
        // Create the skin
        Skin skin = new Skin();

        // Create the pixmap
        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(153 / 255f, 76 / 255f, 0 / 255f, 1));
        bg.fill();
        skin.add("bg", new TextureRegion(new Texture(bg)));
        skin.add("default", font);

        // Create the label style
        TextField.TextFieldStyle textStyle= new TextField.TextFieldStyle();
        textStyle.font = skin.getFont("default");
        textStyle.fontColor = Color.WHITE;
        textStyle.background = skin.getDrawable("bg");
        skin.add("default", textStyle);

        // Create the field
        TextField textField = new TextField(text, skin);
        // Accept only numbers
        textField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        textField.setAlignment(Align.center);
        // Set the default mass
        World.setSelectedMass(Float.parseFloat(text));

        // Custom filter
        TextField.TextFieldFilter decimalFilter = new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                // Allow digits and one decimal point
                String text = textField.getText();
                if (Character.isDigit(c)) {
                    // Allow digits
                    return true;
                }
                if (c == '.' && !text.contains(".")) {
                    // Allow a single decimal point
                    return true;
                }
                // Disallow everything else
                return false;
            }
        };

        textField.setTextFieldFilter(decimalFilter);

        // Add a change listener
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    // If the input is not empty
                    String input = textField.getText();
                    if (input.isEmpty()) return;
                    // Set the mass
                    World.setSelectedMass(Float.parseFloat(input));
                }
                catch (Exception error){
                    System.out.println("Invalid input");
                }
            }
        });

        // Add the input field to the table
        table.add(textField).width(width).height(height);
    }

    // Add the mass rotation input field to the header
    public static void addRotationInputToHeader(Table table, BitmapFont font, String text, float width, float height){
        // Create the skin
        Skin skin = new Skin();

        // Create the pixmap
        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(153 / 255f, 76 / 255f, 0 / 255f, 1));
        bg.fill();
        skin.add("bg", new TextureRegion(new Texture(bg)));
        skin.add("default", font);

        // Create the label style
        TextField.TextFieldStyle textStyle= new TextField.TextFieldStyle();
        textStyle.font = skin.getFont("default");
        textStyle.fontColor = Color.WHITE;
        textStyle.background = skin.getDrawable("bg");
        skin.add("default", textStyle);

        // Create the field
        TextField textField = new TextField(text, skin);
        // Accept only numbers
        textField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        textField.setAlignment(Align.center);
        // Set the default side length
        World.setSelectedCreationRotation(Float.parseFloat(text));

        // Custom filter
        TextField.TextFieldFilter negativeFilter = new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                // Allow digits and one decimal point
                String text = textField.getText();
                if (Character.isDigit(c)) {
                    // Allow digits
                    return true;
                }
                if (c == '-' && text.isEmpty()) {
                    // Allow a single decimal point
                    return true;
                }
                // Disallow everything else
                return false;
            }
        };

        textField.setTextFieldFilter(negativeFilter);

        // Add a change listener
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    String input = textField.getText();
                    if (input.isEmpty()) return;
                    World.setSelectedCreationRotation(Float.parseFloat(input) % 360);
                }
                catch (Exception error){
                    System.out.println("Invalid Input");
                }
            }
        });

        // Add the input field to the table
        table.add(textField).width(width).height(height);
    }

    // Add the side length input field to the header
    public static void addSideLengthInputToHeader(Table table, BitmapFont font, String text, float width, float height){
        // Create the skin
        Skin skin = new Skin();

        // Create the pixmap
        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(153 / 255f, 76 / 255f, 0 / 255f, 1));
        bg.fill();
        skin.add("bg", new TextureRegion(new Texture(bg)));
        skin.add("default", font);

        // Create the label style
        TextField.TextFieldStyle textStyle= new TextField.TextFieldStyle();
        textStyle.font = skin.getFont("default");
        textStyle.fontColor = Color.WHITE;
        textStyle.background = skin.getDrawable("bg");
        skin.add("default", textStyle);

        // Create the field
        TextField textField = new TextField(text, skin);
        // Accept only numbers
        textField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        textField.setAlignment(Align.center);
        // Set the default side length
        World.setSelectedSideLength(Float.parseFloat(text));

        // Add a change listener
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    String input = textField.getText();
                    if (input.isEmpty()) return;
                    World.setSelectedSideLength(Float.parseFloat(input));
                }
                catch (Exception error){
                    System.out.println("Invalid input");
                }
            }
        });

        // Add the input field to the table
        table.add(textField).width(width).height(height);
    }

    // Add the gravity slider option to the header
    public static void addGravitySliderToHeader(Table table, BitmapFont font, float width, float height) {
        // Create a new Skin
        Skin skin = new Skin();

        // Load textures for the slider's background and knob
        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(0 / 255f, 0 / 255f, 102 / 255f, 1));
        bg.fill();
        Pixmap knob = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        knob.setColor(new Color(102 / 255f, 178 / 255f, 255 / 255f, 1));
        knob.fill();

        // Add the textures to the skin
        skin.add("bg", new TextureRegion(new Texture(bg)));
        skin.add("knob", new TextureRegion(new Texture(knob)));

        // Create the slider style
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin.getDrawable("bg"); // Set the background
        sliderStyle.knob = skin.getDrawable("knob"); // Set the knob
        sliderStyle.knob.setMinWidth(width / 11);
        sliderStyle.knob.setMinHeight(height);
        sliderStyle.background.setMinHeight(height);
        skin.add("default-horizontal", sliderStyle);

        // Create the slider
        Slider slider = new Slider(-5, 5, 1, false, skin); // Set min, max, step size
        slider.setStyle(sliderStyle); // Apply the custom style
        slider.setValue(5); // Set the initial value
        World.setSelectedGravity(5);

        // Add a listener to detect value changes
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Get the value of the slider when it changes
                float sliderValue = slider.getValue();
                // Change the gravity
                World.setSelectedGravity(sliderValue);
            }
        });

        // Add the slider to the table with specified width and height
        table.add(slider).width(width);
    }
}
