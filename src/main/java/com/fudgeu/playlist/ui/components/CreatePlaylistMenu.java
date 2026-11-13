package com.fudgeu.playlist.ui.components;

import com.fudgeu.playlist.client.PlaylistClient;
import com.fudgeu.playlist.fluxui.FluxInstance;
import com.fudgeu.playlist.fluxui.State;
import com.fudgeu.playlist.fluxui.builders.ComponentBuilder;
import com.fudgeu.playlist.fluxui.elements.Container;
import com.fudgeu.playlist.fluxui.elements.Element;
import com.fudgeu.playlist.fluxui.elements.FText;
import com.fudgeu.playlist.fluxui.props.BoolProp;
import com.fudgeu.playlist.fluxui.props.PropProvider;
import com.fudgeu.playlist.fluxui.props.PropTemplate;
import com.fudgeu.playlist.fluxui.style.color.Color;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import com.fudgeu.playlist.objects.PlaylistObj;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CreatePlaylistMenu implements ComponentBuilder {
    private FluxInstance fluxInstance;
    private final ResourceLocation componentLocation = ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/create_playlist_menu/layout.xml");
    private final PropTemplate propTemplate;
    private final SolidColor COLOR_INVALID = SolidColor.create(Color.fromHex(16746624));
    private CreatePlaylistMenu.PlaylistProperties playlistProperties;

    public CreatePlaylistMenu() {
        this.propTemplate = new PropTemplate();
        this.propTemplate.requireRunnable("goBack");
    }

    @Override
    public PropProvider build(String id, Container self, PropProvider propProvider, State state) {
        this.fluxInstance = FluxInstance.getInstance();
        PropProvider childProps = new PropProvider();
        this.playlistProperties = new CreatePlaylistMenu.PlaylistProperties();
        BoolProp canCreate = new BoolProp(true);
        Runnable goBack = propProvider.getRunnable("goBack");
        Runnable createPlaylist = () -> {
            this.createPlaylist(this.playlistProperties);
            goBack.run();
        };
        HashMap<String, String> tabs = new HashMap<>();
        tabs.put("general", "General");
        tabs.put("timings", "Timings");
        this.generateTab("general", self, this.playlistProperties);
        this.playlistProperties.isNameFieldEmpty.addUpdateListener((oldVal, newVal) -> {
            if (newVal) {
                canCreate.set(true);
            } else {
                canCreate.set(false);
            }
        });
        childProps.setBoolProp("canCreate", canCreate);
        childProps.setRunnable("createPlaylist", createPlaylist);
        childProps.setRunnable("goBack", goBack);
        childProps.setStringMap("tabs", tabs);
        childProps.setStringConsumer("onTabChange", newSelection -> this.generateTab(newSelection, self, this.playlistProperties));
        return childProps;
    }

    @Override
    public void onLoadFinished(Container self, PropProvider propProvider) {
        this.generateTab("general", self, this.playlistProperties);
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

    private void createPlaylist(CreatePlaylistMenu.PlaylistProperties properties) {
        PlaylistObj newPlaylist = new PlaylistObj(properties.title);
        if (properties.cloneDefaultPlaylist.get()) {
            for (ResourceLocation songId : PlaylistClient.instance.playlistManager.getRegisteredSongs()) {
                newPlaylist.addSong(songId);
            }
        }

        int id = PlaylistClient.instance.playlistManager.registerPlaylist(newPlaylist);
        PlaylistObj playlist = PlaylistClient.instance.playlistManager.getRegisteredPlaylist(id);
        playlist.setTimeBetweenSongs(properties.timeBetweenSongs);
        playlist.setTimeVariance(Math.round(properties.timeBetweenSongs * (properties.variance / 100.0F)));
    }

    private void generateTab(String selection, Container self, CreatePlaylistMenu.PlaylistProperties playlistProperties) {
        Optional<Element> optionsOpt = self.findChildById("CreatePlaylistOptions");
        if (!optionsOpt.isEmpty()) {
            Container optionsContainer = (Container)optionsOpt.get();
            optionsContainer.removeAllChildren();
            switch (selection) {
                case "general":
                    Optional<Element> generalTab = this.createGeneralTab(self, playlistProperties);
                    generalTab.ifPresent(optionsContainer::addChild);
                    break;
                case "timings":
                    PropProvider props = new PropProvider();
                    props.setStringConsumer("onTimeBtwnSongsUpdate", s -> playlistProperties.timeBetweenSongs = Integer.parseInt(s));
                    props.setStringConsumer("onVarianceUpdate", s -> playlistProperties.variance = Integer.parseInt(s));
                    props.setString("timeBtwnSongsDefault", Integer.toString(playlistProperties.timeBetweenSongs));
                    props.setString("varianceDefault", Integer.toString(playlistProperties.variance));
                    Optional<Element> timingsTab = this.fluxInstance.buildComponent("PlaylistTimings", null, props);
                    timingsTab.ifPresent(optionsContainer::addChild);
            }
        }
    }

    private Optional<Element> createGeneralTab(Container self, CreatePlaylistMenu.PlaylistProperties playlistProperties) {
        PropProvider props = new PropProvider();
        Consumer<String> onPlaylistNameUpdate = newText -> {
            if (playlistProperties.isNameFieldEmpty.get() && newText.length() == 0) {
                playlistProperties.isNameFieldEmpty.invert();
            } else if (!playlistProperties.isNameFieldEmpty.get() && newText.length() >= 1) {
                playlistProperties.isNameFieldEmpty.invert();
            }

            playlistProperties.title = newText;
        };
        playlistProperties.isNameFieldEmpty.addUpdateListener((oldVal, newVal) -> self.findChildById("PlaylistNameEmptyError").ifPresent(e -> {
            if (e instanceof FText text) {
                if (newVal) {
                    text.setText(Component.literal(" "));
                } else {
                    text.setText(Component.literal("!"));
                }
            }
        }));
        props.setString("defaultName", playlistProperties.title);
        props.setStringConsumer("onPlaylistNameUpdate", onPlaylistNameUpdate);
        props.setBoolProp("cloneDefaultPlaylist", playlistProperties.cloneDefaultPlaylist);
        props.setRunnable("toggleCloneDefaultPlaylist", playlistProperties.cloneDefaultPlaylist::invert);
        return this.fluxInstance.loadElementFromXML(ResourceLocation.fromNamespaceAndPath("playlist", "fluxui/create_playlist_menu/tabs/general.xml"), props);
    }

    static class PlaylistProperties {
        String title = "My Playlist";
        int timeBetweenSongs = 900;
        int variance = 35;
        BoolProp cloneDefaultPlaylist = new BoolProp(false);
        BoolProp isNameFieldEmpty = new BoolProp(false);
    }
}
