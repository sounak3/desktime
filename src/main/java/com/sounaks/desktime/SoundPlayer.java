package com.sounaks.desktime;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class SoundPlayer extends JComponent implements ActionListener, Runnable
{
    private JTextField txtFile;
    private JButton browser, play;
    private JFileChooser fileChooser;
    private Player player;
    private boolean playing;
    private FileInputStream fStream;
    private Timer playTimer;
    private ImageIcon playIcon, stopIcon, brseIcon;
    public int playSeconds;
    public String audioFile;

    public int getPlayDurationInSeconds()
    {
        return playSeconds;
    }

    public String getAudioFileName()
    {
        return audioFile;
    }

    public void setAudioFileName(String fileName) throws FileNotFoundException
    {
        File newFile = new File(fileName);
        if (newFile.exists())
        {
            audioFile = fileName;
            txtFile.setText(fileName);
        }
        else
        {
            throw new FileNotFoundException(fileName);
        }
    }

    public SoundPlayer(int playSeconds)
    {
		Image playImg    = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/play-icon.png"))).getImage();
		Image stopImg    = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/stop-icon.png"))).getImage();
		Image brseImg    = (new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/folder-icon.png"))).getImage();
        this.playIcon    = new ImageIcon(playImg.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        this.stopIcon    = new ImageIcon(stopImg.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        this.brseIcon    = new ImageIcon(brseImg.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        txtFile          = new JTextField(15);
        browser          = new JButton(brseIcon);
        play             = new JButton(playIcon);
        fileChooser      = new JFileChooser();
        this.playSeconds = playSeconds * 1000;
        this.audioFile   = "";
        fileChooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                String s = f.getName().toLowerCase();
		        return s.endsWith("mp3") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Audio files (*.mp3)";
            }
            
        });
        browser.setActionCommand("SEARCH");
		browser.setToolTipText("Browse mp3 Audio files");
		browser.setMargin(new Insets(2, 5, 2, 5));
        browser.addActionListener(this);
        play.setActionCommand("PLAY_AUDIO_FILE");
		play.setToolTipText("Play selected audio file");
		play.setMargin(new Insets(2, 5, 2, 5));
        play.addActionListener(this);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(txtFile);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(browser);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(play);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        if (cmd.equals("SEARCH"))
        {
            fileChooser.showOpenDialog(SoundPlayer.this);
            File file = fileChooser.getSelectedFile();
            if (file != null)
            {
                this.audioFile = file.getAbsolutePath();
                txtFile.setText(audioFile);
                stopPlayer();
            }
        }
        else if (cmd.equals("PLAY_AUDIO_FILE"))
        {
            File file = new File(audioFile);
            try
            {
                if (file.exists())
                {
                    if (!playing)
                    {
                        fStream = new FileInputStream(file);
                        if (playTimer == null)
                            playTimer = new Timer(playSeconds, this);
                        playTimer.setActionCommand("STOP_AUDIO_FILE");
                        playTimer.setRepeats(false);
                        if (player == null)
                            player = new Player(fStream);
                        playTimer.start();
                        Thread runThread = new Thread(this, "PLAY_THREAD");
                        runThread.start();
                    }
                    else
                    {
                        stopPlayer();
                    }
                }
                else
                {
                    String msg = "";
                    if (audioFile.isEmpty()) msg = "No file to play! Please select audio file.";
                    else msg = "The selected file does not exist.";
                    JOptionPane.showMessageDialog(SoundPlayer.this,
                            msg,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (Exception me)
            {
                stopPlayer();
                me.printStackTrace();
                JOptionPane.showMessageDialog(SoundPlayer.this,
                            "Cannot play selected audio file. Unsupported format!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (cmd.equals("STOP_AUDIO_FILE"))
        {
            stopPlayer();
        }
    }

    public void stopPlayer()
    {
        if (player != null)
        {
            player.close();
            playTimer.stop();
            play.setIcon(playIcon);
            play.setToolTipText("Play selected audio file");
            try
            {
                fStream.close();                    
            } 
            catch (IOException ioe) 
            {
                ioe.printStackTrace();
            }
            player = null;
            playTimer = null;
            playing = false;
        }
    }

    public void run()
    {
        try {
            if (player != null && !playing)
            {
                playing = true;
                play.setIcon(stopIcon);
                play.setToolTipText("Stop playing audio file");
                player.play();
                playing = false;
            }
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public static Player playAudio(File fileToPlay, int playSec) throws FileNotFoundException, JavaLayerException
    {
        if (fileToPlay.exists())
        {
            FileInputStream fins = new FileInputStream(fileToPlay);
            String threadName = "THREAD_" + fileToPlay.getName().toUpperCase().replaceAll("\\W", ""). replace(".mp3", "");
            Player sPlayer = new Player(fins);
            int playMillis = playSec * 1000;
            Timer tPlay = new Timer(playMillis, new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        sPlayer.close();
                        fins.close();
                    } catch (Exception ioe) {
                        ioe.printStackTrace();
                    }
                }
            });
            Thread playerThread = new Thread(threadName) {
                public void run() {
                    try {
                        tPlay.start();
                        sPlayer.play();
                    } catch (Exception je) {
                        je.printStackTrace();
                    }
                }
            };
            playerThread.start();
            return sPlayer;
        }
        else
        {
            throw new FileNotFoundException(fileToPlay.getAbsolutePath());
        }
    }

    public static boolean stopAudio(Player playerToStop)
    {
        try {
            playerToStop.close();
            playerToStop = null;
            return true;
        } catch (Exception ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    // public static void main(String[] args) {
    //     JFrame frame = new JFrame("test");
    //     SoundPlayer ch = new SoundPlayer(30);
    //     frame.getContentPane().add(ch);
    //     frame.pack();
    //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     frame.setVisible(true);
    // }
}
