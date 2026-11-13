package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.elements.TextBox;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

public class PlaylistTimings implements ComponentBuilder {
    private final ResourceLocation componentLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/playlist_timings/layout.xml");
    private final PropTemplate propTemplate = new PropTemplate();

    public PlaylistTimings() {
        this.propTemplate.requireStringConsumer("onTimeBtwnSongsUpdate");
        this.propTemplate.requireStringConsumer("onVarianceUpdate");
        this.propTemplate.requireString("timeBtwnSongsDefault");
        this.propTemplate.requireString("varianceDefault");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        PropProvider childProps = new PropProvider();
        childProps.setString("timeBtwnSongsDefault", propProvider.getString("timeBtwnSongsDefault"));
        childProps.setString("varianceDefault", propProvider.getString("varianceDefault"));
        Consumer<String> onTimeBtwnSongsUpdate = propProvider.getStringConsumer("onTimeBtwnSongsUpdate");
        childProps.setRunnable("timeBtwnSongsUnFocus", () -> {
            Optional<Element> textBoxOpt = self.findChildById("TimeBtwnSongsTextBox");
            if (!textBoxOpt.isEmpty()) {
                Element textBoxElement = textBoxOpt.get();
                if (textBoxElement instanceof TextBox textBox) {
                    String textBoxContents = textBox.getText();
                    if (textBoxContents.length() == 0) {
                        String defaultValue = textBox.getDefaultLabel().getString();
                        textBox.setText(defaultValue);
                        textBoxContents = defaultValue;
                    }

                    onTimeBtwnSongsUpdate.accept(textBoxContents);
                }
            }
        });
        Consumer<String> onVarianceUpdate = propProvider.getStringConsumer("onVarianceUpdate");
        childProps.setRunnable("varianceUnFocus", () -> {
            Optional<Element> textBoxOpt = self.findChildById("VarianceTextBox");
            if (!textBoxOpt.isEmpty()) {
                Element textBoxElement = textBoxOpt.get();
                if (textBoxElement instanceof TextBox textBox) {
                    String textBoxContents = textBox.getText();
                    if (textBoxContents.length() == 0) {
                        String defaultValue = textBox.getDefaultLabel().getString();
                        textBox.setText(defaultValue);
                        textBoxContents = defaultValue;
                    }

                    onVarianceUpdate.accept(textBoxContents);
                }
            }
        });
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
    }

    @Override
    public void refresh() {
    }

    @Override
    public PropTemplate getPropTemplate() {
        return this.propTemplate;
    }

    @Override
    public ResourceLocation getComponentLocation() {
        return this.componentLocation;
    }
}
