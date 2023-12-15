package net.tv.view.config;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.jtattoo.plaf.acryl.AcrylLookAndFeel;
import com.jtattoo.plaf.aero.AeroLookAndFeel;
import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import com.jtattoo.plaf.bernstein.BernsteinLookAndFeel;
import com.jtattoo.plaf.fast.FastLookAndFeel;
import com.jtattoo.plaf.graphite.GraphiteLookAndFeel;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import com.jtattoo.plaf.luna.LunaLookAndFeel;
import com.jtattoo.plaf.mcwin.McWinLookAndFeel;
import com.jtattoo.plaf.mint.MintLookAndFeel;
import com.jtattoo.plaf.noire.NoireLookAndFeel;
import com.jtattoo.plaf.smart.SmartLookAndFeel;
import com.jtattoo.plaf.texture.TextureLookAndFeel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThemeConfig {

    public enum Key {
        FlatDarculaLaf(new FlatDarculaLaf()),
        FlatDarkLaf(new FlatDarkLaf()),
        FlatIntelliJLaf(new FlatIntelliJLaf()),
        FlatLightLaf(new FlatLightLaf()),
        FlatMacDarkLaf(new FlatMacDarkLaf()),
        FlatMacLightLaf(new FlatMacLightLaf()),
        MetalLookAndFeel(new MetalLookAndFeel()),
        MotifLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
        NimbusLookAndFeel(new NimbusLookAndFeel()),
        SimpleFlatLightLaf(new FlatLightLaf()),
        WindowsClassicLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"),
        WindowsLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"),
        AcrylLookAndFeel(new AcrylLookAndFeel()),
        AeroLookAndFeel(new AeroLookAndFeel()),
        AluminiumLookAndFeel(new AluminiumLookAndFeel()),
        BernsteinLookAndFeel(new BernsteinLookAndFeel()),
        FastLookAndFeel(new FastLookAndFeel()),
        GraphiteLookAndFeel(new GraphiteLookAndFeel()),
        HiFiLookAndFeel(new HiFiLookAndFeel()),
        LunaLookAndFeel(new LunaLookAndFeel()),
        McWinLookAndFeel(new McWinLookAndFeel()),
        MintLookAndFeel(new MintLookAndFeel()),
        NoireLookAndFeel(new NoireLookAndFeel()),
        SmartLookAndFeel(new SmartLookAndFeel()),
        TextureLookAndFeenewl(new TextureLookAndFeel()),
        ;
        public LookAndFeel theme;

        public String className;

        Key(LookAndFeel theme) {
            this.theme = theme;
        }

        Key(String className) {
            this.className = className;
        }

        public static Key getOrDefault(String keyName) {
            try {
                return valueOf(keyName);
            } catch (Exception e) {
                return getDefault().getSystem();
            }
        }
    }

    /**
     * 整体主题
     */
    private Key system;

    public static ThemeConfig getDefault() {
        return ThemeConfig.builder()
                .system(Key.FlatMacDarkLaf)
                .build();
    }

}
