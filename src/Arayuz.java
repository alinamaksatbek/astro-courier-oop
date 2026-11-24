import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/** Kozmik Doga: Java Swing/Java2D ile, dis kutuphane olmadan goruntuleme. */
final class Arayuz {
    private static final Color BG=new Color(12,28,27), PANEL=new Color(21,47,43), CARD=new Color(29,60,53);
    private static final Color LINE=new Color(65,98,83), FG=new Color(242,240,220), MUTED=new Color(174,195,175);
    private static final Color GOLD=new Color(239,164,91), GREEN=new Color(144,213,162);
    private static final Color[] ROBOT_COLORS={new Color(244,143,106),new Color(178,151,236),new Color(117,215,165),new Color(105,190,222),new Color(240,191,105),new Color(205,126,166),new Color(142,204,207),new Color(200,207,142),new Color(233,135,126)};
    private final JFrame window=new JFrame("Gezegen Postacilari | Kozmik Doga");
    private final JTextField mapPath=new JTextField("ornekler/harita.txt",18), cmdPath=new JTextField("ornekler/komutlar.txt",18);
    private final JLabel title=new JLabel("Gorev hazir"), counter=new JLabel("Kare 0/0"), stats=new JLabel("Teslim 0/0  •  Enerji 0"), event=new JLabel("Gorev dosyalarini secin");
    private final JButton play=new JButton("▶ Oynat"), back=new JButton("◀ Geri"), next=new JButton("Ileri ▶"), first=new JButton("|◀ Basa"), last=new JButton("Sona ▶|");
    private final JSlider speed=new JSlider(65,850,280);
    private final JPanel robotCards=new JPanel();
    private final JPanel sideCommands=new JPanel();
    private final JLabel commandHint=new JLabel("Komut secerek ilgili ana gidebilirsiniz");
    private final MapView mapView=new MapView();
    private final List<Frame> frames=new ArrayList<>();
    private final javax.swing.Timer timer=new javax.swing.Timer(280,e->advance());
    private Harita terrain;
    private List<Komut> commands=List.of();
    private int index=0, initialEnergy=1;
    private static final class State {
        final int id,x,y,energy; final Yon direction;final Durum status;final String body;
        State(Robot r){id=r.no();x=r.x();y=r.y();energy=r.enerji();direction=r.yon();status=r.durum();body=r.govde().ad();}
    }
    private static final class Frame {
        final List<State> robots=new ArrayList<>();final String label;final int commandNumber,activeId;
        Frame(Harita h,String label,int number,int robotId){for(Robot r:h.robotlar())robots.add(new State(r));this.label=label;commandNumber=number;activeId=robotId;}
    }
    private Arayuz(){}
    static void baslat(){SwingUtilities.invokeLater(()->new Arayuz().show());}
    private JLabel text(String s,int size,Color color,boolean bold){JLabel l=new JLabel(s);l.setForeground(color);l.setFont(new Font("SansSerif",bold?Font.BOLD:Font.PLAIN,size));return l;}
    private JPanel panel(LayoutManager layout){JPanel p=new JPanel(layout);p.setBackground(PANEL);p.setBorder(new EmptyBorder(14,16,14,16));return p;}
    private JButton button(String s){JButton b=new JButton(s);b.setFocusPainted(false);b.setBackground(CARD);b.setForeground(FG);b.setFont(new Font("SansSerif",Font.BOLD,12));b.setBorder(new CompoundBorder(new LineBorder(LINE,1,true),new EmptyBorder(9,12,9,12)));b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private void format(JTextField f){f.setBackground(CARD);f.setForeground(FG);f.setCaretColor(FG);f.setFont(new Font("SansSerif",Font.PLAIN,13));f.setBorder(new CompoundBorder(new LineBorder(LINE,1,true),new EmptyBorder(9,10,9,10)));}
    private JPanel header(){
        JPanel header=new JPanel(new BorderLayout(0,12)){
            protected void paintComponent(Graphics gr){super.paintComponent(gr);Graphics2D g=(Graphics2D)gr.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int w=getWidth(),h=getHeight();g.setPaint(new GradientPaint(0,0,new Color(12,34,31),w,h,new Color(35,63,46)));g.fillRect(0,0,w,h);
                g.setColor(new Color(242,173,103,160));g.fillOval(w-215,-82,168,168);
                g.setColor(new Color(26,53,45));g.fillOval(w-232,-73,168,168);
                g.setColor(new Color(79,112,78,130));
                Path2D mountain=new Path2D.Double();mountain.moveTo(0,h);mountain.lineTo(w*.35,h*.35);mountain.lineTo(w*.51,h*.72);mountain.lineTo(w*.69,h*.27);mountain.lineTo(w,h);mountain.closePath();g.fill(mountain);
                g.setColor(new Color(184,219,166,95));for(int i=0;i<28;i++){int x=(i*197+43)%Math.max(1,w),y=(i*53+13)%Math.max(1,h/2);g.fillOval(x,y,2+(i%3),2+(i%3));}g.dispose();
            }
        };header.setOpaque(false);header.setBorder(new EmptyBorder(18,20,12,20));
        JPanel top=new JPanel(new BorderLayout());top.setOpaque(false);
        JPanel headings=new JPanel(new GridLayout(2,1,0,4));headings.setOpaque(false);
        headings.add(text("◉  GEZEGEN POSTACILARI",26,FG,true));
        headings.add(text("KOZMIK DOGA   /   GEZEGEN KESFI   /   CANLI TELEMETRI",12,MUTED,true));top.add(headings,BorderLayout.WEST);
        JLabel tag=text("MISSION CONTROL  •  PROLAB 2",12,GOLD,true);top.add(tag,BorderLayout.EAST);header.add(top,BorderLayout.NORTH);
        JPanel inputs=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));inputs.setBackground(new Color(23,52,45));inputs.setBorder(new EmptyBorder(6,9,6,9));
        format(mapPath);format(cmdPath);
        inputs.add(text("HARITA",12,MUTED,true));inputs.add(mapPath);
        JButton selectMap=button("Dosya sec");selectMap.addActionListener(e->choose(mapPath));inputs.add(selectMap);
        inputs.add(Box.createHorizontalStrut(8));inputs.add(text("KOMUTLAR",12,MUTED,true));inputs.add(cmdPath);
        JButton selectCmd=button("Dosya sec");selectCmd.addActionListener(e->choose(cmdPath));inputs.add(selectCmd);
        JButton load=button("▶  Gorevi Baslat");load.setBackground(GOLD);load.setForeground(BG);load.addActionListener(e->load());inputs.add(load);
        header.add(inputs,BorderLayout.CENTER);return header;
    }
    private JPanel mapSection(){
        JPanel p=panel(new BorderLayout(0,8));
        JPanel heading=new JPanel(new BorderLayout());heading.setBackground(PANEL);
        heading.add(text("GEZEGEN HARITASI",17,FG,true),BorderLayout.WEST);
        heading.add(text("Renkli izler: gercek robot rotalari",12,MUTED,false),BorderLayout.EAST);
        p.add(heading,BorderLayout.NORTH);
        JPanel body=new JPanel(new BorderLayout(10,0));body.setBackground(PANEL);
        JPanel commandSide=panel(new BorderLayout(0,9));commandSide.setBackground(new Color(23,49,42));
        commandSide.setBorder(new CompoundBorder(new LineBorder(LINE,1,true),new EmptyBorder(12,10,12,10)));
        commandSide.add(text("KOMUTLAR",14,FG,true),BorderLayout.NORTH);
        sideCommands.setLayout(new BoxLayout(sideCommands,BoxLayout.Y_AXIS));sideCommands.setBackground(new Color(23,49,42));
        JScrollPane sideScroll=new JScrollPane(sideCommands);sideScroll.setBorder(null);sideScroll.getViewport().setBackground(new Color(23,49,42));
        sideScroll.getVerticalScrollBar().setUnitIncrement(18);commandSide.add(sideScroll,BorderLayout.CENTER);
        commandHint.setFont(new Font("SansSerif",Font.PLAIN,10));commandHint.setForeground(MUTED);
        commandSide.add(commandHint,BorderLayout.SOUTH);commandSide.setPreferredSize(new Dimension(192,260));
        body.add(commandSide,BorderLayout.WEST);body.add(mapView,BorderLayout.CENTER);
        p.add(body,BorderLayout.CENTER);p.setMinimumSize(new Dimension(450,260));return p;
    }
    private JPanel commandSection(){
        JPanel p=panel(new BorderLayout(0,9));
        p.setMinimumSize(new Dimension(300,170));
        JPanel heading=new JPanel(new BorderLayout());heading.setBackground(PANEL);
        heading.add(text("GOREV KONTROLU  /  CANLI SONUCLAR",15,FG,true),BorderLayout.WEST);
        counter.setForeground(MUTED);counter.setFont(new Font("SansSerif",Font.BOLD,12));
        heading.add(counter,BorderLayout.EAST);p.add(heading,BorderLayout.NORTH);
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT,7,4));controls.setBackground(PANEL);
        for(JButton b:new JButton[]{first,back,play,next,last}){
            b.setBackground(CARD);b.setForeground(FG);
            b.setBorder(new CompoundBorder(new LineBorder(LINE,1,true),new EmptyBorder(8,11,8,11)));
            controls.add(b);
        }
        play.setBackground(GOLD);play.setForeground(BG);
        controls.add(text("  HIZ",12,MUTED,true));speed.setBackground(PANEL);
        speed.setPreferredSize(new Dimension(115,24));controls.add(speed);
        p.add(controls,BorderLayout.CENTER);
        JPanel result=new JPanel(new GridLayout(3,1,2,4));result.setBackground(PANEL);
        title.setFont(new Font("SansSerif",Font.BOLD,14));title.setForeground(FG);
        stats.setFont(new Font("SansSerif",Font.BOLD,20));stats.setForeground(GREEN);
        event.setFont(new Font("SansSerif",Font.PLAIN,12));event.setForeground(MUTED);
        result.add(title);result.add(stats);result.add(event);
        p.add(result,BorderLayout.SOUTH);return p;
    }
    private JPanel robotsSection(){
        JPanel p=panel(new BorderLayout(0,10));p.setMinimumSize(new Dimension(270,270));
        p.add(text("ROBOT TELEMETRISI",17,FG,true),BorderLayout.NORTH);
        robotCards.setBackground(PANEL);robotCards.setLayout(new BoxLayout(robotCards,BoxLayout.Y_AXIS));
        JScrollPane scroll=new JScrollPane(robotCards,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);scroll.setBorder(null);scroll.getViewport().setBackground(PANEL);scroll.getVerticalScrollBar().setUnitIncrement(18);
        p.add(scroll,BorderLayout.CENTER);return p;
    }
    private static final String[][] LEGEND={
        {".","DUZ ZEMIN","Tum govdeler: 1 enerji."},
        {"T","TOZ FIRTINASI","Havali 1 enerji harcar, savrulur. Digerleri 2 enerji."},
        {"K","KRATERLI KAYA","Paletli/Kizakli 3, Havali 1. Sicrayan atlar: 2."},
        {"~","METAN GOLU","Yalnizca ucan veya yuzen govdeler gecer: 1."},
        {"I","BUZ KABUGU","Kizakli 1 enerjiyle kayar. Havali 1, digerleri 2."},
        {"X","ENGEL","Robotlar gecemez."},
        {"H","TESLIM RAMPASI","Giren robot paketi teslim eder: 1 enerji."},
        {"L","LAV (OZGUN)","Yalnizca ucan govde gecer: 4 enerji."}
    };
    private JPanel legendSection(){
        JPanel p=panel(new BorderLayout(0,8));p.setMinimumSize(new Dimension(270,170));
        p.add(text("ZEMIN REHBERI",17,FG,true),BorderLayout.NORTH);
        JPanel list=new JPanel();list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));list.setBackground(PANEL);
        for(String[] item:LEGEND){
            JPanel row=new JPanel(new BorderLayout(12,0));row.setBackground(PANEL);row.setBorder(new EmptyBorder(7,1,7,2));row.setMaximumSize(new Dimension(Integer.MAX_VALUE,88));
            char symbol=item[0].charAt(0);
            JComponent icon=new JComponent(){protected void paintComponent(Graphics g){Graphics2D a=(Graphics2D)g.create();a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);drawTile(a,symbol,3,3,43);a.dispose();}};
            icon.setPreferredSize(new Dimension(49,49));row.add(icon,BorderLayout.WEST);
            JPanel desc=new JPanel(new BorderLayout(0,2));desc.setBackground(PANEL);
            desc.add(text(item[1],13,FG,true),BorderLayout.NORTH);
            JTextArea detail=new JTextArea(item[2]);detail.setLineWrap(true);detail.setWrapStyleWord(true);detail.setEditable(false);detail.setOpaque(false);detail.setForeground(MUTED);detail.setFont(new Font("SansSerif",Font.PLAIN,12));detail.setFocusable(false);
            desc.add(detail,BorderLayout.CENTER);row.add(desc,BorderLayout.CENTER);list.add(row);
        }
        JScrollPane scroll=new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);scroll.setBorder(null);scroll.getViewport().setBackground(PANEL);scroll.getVerticalScrollBar().setUnitIncrement(16);p.add(scroll,BorderLayout.CENTER);return p;
    }
    private void show(){
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);window.getContentPane().setBackground(BG);window.setLayout(new BorderLayout(10,10));
        window.add(header(),BorderLayout.NORTH);
        JSplitPane left=new JSplitPane(JSplitPane.VERTICAL_SPLIT,mapSection(),commandSection());left.setResizeWeight(.72);left.setContinuousLayout(true);left.setDividerSize(7);left.setBorder(null);
        JSplitPane right=new JSplitPane(JSplitPane.VERTICAL_SPLIT,robotsSection(),legendSection());right.setResizeWeight(.56);right.setContinuousLayout(true);right.setDividerSize(7);right.setBorder(null);
        JSplitPane main=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,left,right);main.setResizeWeight(.69);main.setContinuousLayout(true);main.setDividerSize(8);main.setBorder(null);
        window.add(main,BorderLayout.CENTER);
        first.addActionListener(e->showFrame(0));back.addActionListener(e->showFrame(index-1));next.addActionListener(e->advance());last.addActionListener(e->showFrame(frames.size()-1));
        play.addActionListener(e->{if(timer.isRunning())stop();else if(!frames.isEmpty()){if(index>=frames.size()-1)showFrame(0);timer.setDelay(speed.getValue());timer.start();play.setText("❚❚ Duraklat");}});
        speed.addChangeListener(e->timer.setDelay(speed.getValue()));
        window.setMinimumSize(new Dimension(1080,690));window.setSize(1540,920);window.setLocationRelativeTo(null);window.setVisible(true);
        SwingUtilities.invokeLater(()->{main.setDividerLocation(.69);left.setDividerLocation(.72);right.setDividerLocation(.56);});load();
    }
    private void choose(JTextField f){JFileChooser chooser=new JFileChooser();try{Path p=Path.of(f.getText().trim());if(Files.exists(p))chooser.setSelectedFile(p.toAbsolutePath().toFile());}catch(Exception ignored){}if(chooser.showOpenDialog(window)==JFileChooser.APPROVE_OPTION)f.setText(chooser.getSelectedFile().getAbsolutePath());}
    private void stop(){timer.stop();play.setText("▶ Oynat");}
    private void load(){stop();frames.clear();try{
        Harita h=DosyaOkuyucu.harita(Path.of(mapPath.getText().trim()));List<Komut> cs=DosyaOkuyucu.komutlar(Path.of(cmdPath.getText().trim()));
        terrain=h;commands=cs;initialEnergy=h.robotlar().isEmpty()?1:h.robotlar().get(0).enerji();
        frames.add(new Frame(h,"Baslangic",0,0));
        for(int k=0;k<cs.size();k++){
            final int commandNo=k+1;final String name=cs.get(k).aciklama();
            for(Robot r:h.robotlar()){
                if(r.durum()!=Durum.AKTIF)continue;
                final int id=r.no();final int before=frames.size();
                Robot.hareketDinleyicisi=()->frames.add(new Frame(h,"Komut "+commandNo+" / "+name+" / R"+id,commandNo,id));
                try{cs.get(k).uygula(r,h);}finally{Robot.hareketDinleyicisi=null;}
                if(frames.size()==before)frames.add(new Frame(h,"Komut "+commandNo+" / R"+id+" / hareket yok",commandNo,id));
            }
        }
        if(frames.size()==1)frames.add(new Frame(h,"Komut yok",0,0));showFrame(0);
    }catch(Exception ex){Robot.hareketDinleyicisi=null;terrain=null;commands=List.of();frames.clear();robotCards.removeAll();robotCards.revalidate();mapView.repaint();title.setText("Dosya hatasi");stats.setText("Gorev baslatilamadi");event.setText(ex.getMessage());JOptionPane.showMessageDialog(window,ex.getMessage(),"Gorev dosyasi hatasi",JOptionPane.ERROR_MESSAGE);}}
    private void advance(){if(frames.isEmpty()||index>=frames.size()-1){stop();return;}showFrame(index+1);if(index==frames.size()-1)stop();}
    private void showFrame(int n){if(frames.isEmpty())return;index=Math.max(0,Math.min(frames.size()-1,n));Frame f=frames.get(index);
        title.setText(f.label);counter.setText("Kare "+(index+1)+" / "+frames.size());
        int delivered=0,energy=0;for(State s:f.robots){if(s.status==Durum.TESLIM)delivered++;energy+=s.energy;}
        stats.setText("✓  Teslim: "+delivered+" / "+f.robots.size()+"      ⚡  Kalan enerji: "+energy);
        event.setText(f.activeId==0?"Robotlar doguya bakarak baslar":"Islemdeki robot: R"+f.activeId+"   •   Komut "+f.commandNumber);
        
        sideCommands.removeAll();
        for(int i=0;i<commands.size();i++){
            final int cmdIndex=i;JButton item=button(String.format("%02d   %s",i+1,commands.get(i).aciklama()));
            item.setHorizontalAlignment(SwingConstants.LEFT);item.setAlignmentX(Component.LEFT_ALIGNMENT);
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE,43));
            item.setBackground(i+1==f.commandNumber?GOLD:new Color(31,67,55));
            item.setForeground(i+1==f.commandNumber?BG:FG);
            item.addActionListener(e->{for(int j=0;j<frames.size();j++)if(frames.get(j).commandNumber==cmdIndex+1){showFrame(j);break;}});
            sideCommands.add(item);sideCommands.add(Box.createVerticalStrut(7));
        }
        sideCommands.revalidate();sideCommands.repaint();updateCards(f);mapView.repaint();
    }
    private void updateCards(Frame f){
        robotCards.removeAll();
        for(State s:f.robots){
            Color color=ROBOT_COLORS[(s.id-1)%ROBOT_COLORS.length];
            JPanel card=new JPanel(new BorderLayout(8,0));card.setBackground(CARD);
            card.setBorder(new CompoundBorder(new EmptyBorder(0,0,5,0),
                new CompoundBorder(new LineBorder(LINE,1,true),new EmptyBorder(6,9,6,9))));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE,79));
            card.setPreferredSize(new Dimension(270,79));
            JComponent avatar=new JComponent(){protected void paintComponent(Graphics graphics){
                Graphics2D g=(Graphics2D)graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                drawRobot(g,23,28,14,s.body,s.direction,color);g.dispose();
            }};
            avatar.setPreferredSize(new Dimension(49,60));card.add(avatar,BorderLayout.WEST);
            JPanel data=new JPanel(new BorderLayout(0,4));data.setBackground(CARD);
            JPanel firstRow=new JPanel(new BorderLayout());firstRow.setBackground(CARD);
            firstRow.add(text("R"+s.id+"  "+s.body,13,color,true),BorderLayout.WEST);
            String status=s.status==Durum.TESLIM?"TESLIM":s.status==Durum.ENERJISIZ?"ENERJISIZ":"AKTIF";
            firstRow.add(text(status,11,s.status==Durum.ENERJISIZ?GOLD:GREEN,true),BorderLayout.EAST);
            data.add(firstRow,BorderLayout.NORTH);
            JPanel middle=new JPanel(new BorderLayout());middle.setBackground(CARD);
            middle.add(text("Konum ("+s.x+", "+s.y+")  Yon "+s.direction,11,FG,false),BorderLayout.WEST);
            middle.add(text("Enerji "+s.energy,11,FG,true),BorderLayout.EAST);
            data.add(middle,BorderLayout.CENTER);
            JProgressBar bar=new JProgressBar(0,Math.max(1,initialEnergy));
            bar.setValue(s.energy);bar.setForeground(color);bar.setBackground(new Color(45,70,61));
            bar.setBorderPainted(false);bar.setPreferredSize(new Dimension(150,7));
            data.add(bar,BorderLayout.SOUTH);
            card.add(data,BorderLayout.CENTER);robotCards.add(card);
        }
        robotCards.revalidate();robotCards.repaint();
    }
    private static State find(Frame f,int id){for(State s:f.robots)if(s.id==id)return s;return null;}
    private static Color terrainColor(char c){switch(c){case 'T':return new Color(163,116,65);case 'K':return new Color(110,111,102);case '~':return new Color(33,116,127);case 'I':return new Color(122,188,200);case 'X':return new Color(65,76,84);case 'H':return new Color(196,108,62);case 'L':return new Color(170,65,54);default:return new Color(48,80,67);}}
    private static void drawTile(Graphics2D g,char type,int x,int y,int s){
        Graphics2D a=(Graphics2D)g.create();a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        a.setColor(terrainColor(type));a.fillRoundRect(x+1,y+1,s-2,s-2,Math.max(4,s/8),Math.max(4,s/8));
        if(s<15){a.dispose();return;}
        float scale=s/44f; a.translate(x,y);a.scale(scale,scale);a.setStroke(new BasicStroke(1.8f));
        switch(type){
            case 'T':
                a.setColor(new Color(218,177,110));for(int i=0;i<9;i++){int dx=6+(i*13)%32,dy=7+(i*19)%30;a.fillOval(dx,dy,3+(i%2),3+(i%2));}break;
            case 'K':
                a.setColor(new Color(65,72,68));a.fillOval(8,10,29,25);a.setColor(new Color(158,160,142));a.drawOval(8,10,29,25);a.drawArc(13,15,18,13,190,210);a.setColor(new Color(186,187,167,130));a.fillOval(10,11,4,4);a.fillOval(31,28,3,3);break;
            case '~':
                a.setColor(new Color(116,220,215));for(int i=0;i<3;i++){int yy=11+i*10;a.draw(new QuadCurve2D.Float(3,yy,11,yy-7,20,yy));a.draw(new QuadCurve2D.Float(20,yy,30,yy+7,40,yy));}break;
            case 'I':
                a.setColor(new Color(222,247,241));a.drawLine(7,34,21,17);a.drawLine(21,17,34,8);a.drawLine(21,17,31,25);a.drawLine(16,24,10,13);break;
            case 'X':
                a.setColor(new Color(24,37,40));a.fillRoundRect(6,9,32,27,5,5);a.setColor(new Color(135,151,148));a.drawRoundRect(6,9,32,27,5,5);a.drawLine(10,31,34,13);a.drawLine(10,13,34,31);break;
            case 'H':
                a.setColor(new Color(252,193,99));a.setStroke(new BasicStroke(3));for(int i=4;i<44;i+=12)a.drawLine(i,39,Math.min(41,i+21),8);a.setColor(new Color(255,236,180));a.fillRoundRect(15,12,15,19,4,4);break;
            case 'L':
                a.setColor(new Color(255,155,68));a.setStroke(new BasicStroke(3));a.draw(new QuadCurve2D.Float(3,32,12,15,21,25));a.draw(new QuadCurve2D.Float(21,25,32,6,41,14));a.draw(new QuadCurve2D.Float(3,13,16,7,26,14));break;
            default:
                a.setColor(new Color(72,109,87));a.fillOval(8,8,3,3);a.fillOval(31,30,3,3);
        }
        a.dispose();
    }
    private static void drawRobot(Graphics2D g,int cx,int cy,int size,String body,Yon dir,Color color){
        Graphics2D a=(Graphics2D)g.create();a.translate(cx,cy);
        a.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),55));a.fillOval(-size-5,-size-5,2*size+10,2*size+10);
        a.setStroke(new BasicStroke(Math.max(1.7f,size/8f),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));a.setColor(color);
        if("HAVALI".equals(body)){a.fillOval(-size/3,-size/3,size*2/3,size*2/3);a.drawLine(-size,0,size,0);a.drawLine(0,-size,0,size);for(int dx=-1;dx<=1;dx+=2)for(int dy=-1;dy<=1;dy+=2)a.drawOval(dx*size*3/4-size/5,dy*size*3/4-size/5,size*2/5,size*2/5);}
        else if("SICRAYAN".equals(body)){a.drawRoundRect(-size/2,-size/2,size,size,7,7);a.drawLine(-size/2,size/2,-size,size);a.drawLine(size/2,size/2,size,size);a.drawLine(-size/2,-size/2,-size,-size/2);a.drawLine(size/2,-size/2,size,-size/2);a.fillOval(-size/5,-size/5,size*2/5,size*2/5);}
        else if("KIZAKLI".equals(body)){a.fillRoundRect(-size/2,-size/2,size,size,8,8);a.drawLine(-size,-size/2,-size,size);a.drawLine(size,-size/2,size,size);a.drawLine(-size,size,size,size);}
        else if("YUZEN".equals(body)){a.fillOval(-size/2,-size/2,size,size);a.drawArc(-size,-size/3,size*2,size,180,180);a.drawArc(-size,size/8,size*2,size,180,180);}
        else {a.fillRoundRect(-size/2,-size/2,size,size,6,6);a.drawRoundRect(-size,-size/2,size/2,size,5,5);a.drawRoundRect(size/2,-size/2,size/2,size,5,5);}
        a.setColor(new Color(245,246,220,225));
        a.fillRoundRect(-size/4,-size/5,Math.max(4,size/2),Math.max(4,size/3),Math.max(3,size/6),Math.max(3,size/6));
        a.setColor(new Color(44,76,73));a.fillOval(-Math.max(2,size/10),-Math.max(2,size/10),Math.max(4,size/5),Math.max(4,size/5));
        a.setColor(Color.WHITE);a.setStroke(new BasicStroke(Math.max(1.6f,size/9f)));int dx=dir.dx,dy=dir.dy;
        a.drawLine(dx*size/4,dy*size/4,dx*size*3/5,dy*size*3/5);
        a.drawLine(dx*size*3/5,dy*size*3/5,dx*size*3/5-dx*size/5-dy*size/5,dy*size*3/5-dy*size/5+dx*size/5);
        a.drawLine(dx*size*3/5,dy*size*3/5,dx*size*3/5-dx*size/5+dy*size/5,dy*size*3/5-dy*size/5-dx*size/5);
        a.dispose();
    }
    private final class MapView extends JPanel {
        MapView(){setBackground(new Color(15,38,35));setMinimumSize(new Dimension(230,170));}
        protected void paintComponent(Graphics graphics){super.paintComponent(graphics);if(terrain==null||frames.isEmpty())return;
            Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=terrain.genislik(),h=terrain.yukseklik();int cell=Math.max(2,Math.min(64,Math.min(Math.max(2,getWidth()-38)/w,Math.max(2,getHeight()-38)/h)));
            int ox=(getWidth()-w*cell)/2,oy=(getHeight()-h*cell)/2;
            g.setColor(new Color(52,87,71,80));g.setStroke(new BasicStroke(1));
            for(int gx=16;gx<getWidth();gx+=42)g.drawLine(gx,0,gx,getHeight());
            for(int gy=16;gy<getHeight();gy+=42)g.drawLine(0,gy,getWidth(),gy);
            g.setColor(new Color(239,164,91,35));g.fillRoundRect(ox-12,oy-12,w*cell+24,h*cell+24,22,22);
            for(int y=0;y<h;y++)for(int x=0;x<w;x++)drawTile(g,terrain.simge(x,y),ox+x*cell,oy+y*cell,cell);
            for(int id=1;id<=9;id++){
                Color c=ROBOT_COLORS[id-1];g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),112));g.setStroke(new BasicStroke(Math.max(2,cell/12f),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));State prev=null;
                for(int k=0;k<=index;k++){State cur=find(frames.get(k),id);if(cur==null)continue;
                    if(prev!=null&&(cur.x!=prev.x||cur.y!=prev.y))g.drawLine(ox+prev.x*cell+cell/2,oy+prev.y*cell+cell/2,ox+cur.x*cell+cell/2,oy+cur.y*cell+cell/2);prev=cur;
                }
            }
            Frame f=frames.get(index);
            for(State s:f.robots){
                int cx=ox+s.x*cell+cell/2,cy=oy+s.y*cell+cell/2;
                Color color=ROBOT_COLORS[(s.id-1)%ROBOT_COLORS.length];
                if(s.status==Durum.TESLIM){
                    g.setColor(color);g.setStroke(new BasicStroke(Math.max(2,cell/15f)));
                    g.drawOval(cx-cell/4,cy-cell/4,cell/2,cell/2);
                    g.drawLine(cx-cell/9,cy,cx,cy+cell/10);
                    g.drawLine(cx,cy+cell/10,cx+cell/6,cy-cell/9);
                }else{
                    g.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),90));
                    g.setStroke(new BasicStroke(2f));g.drawOval(cx-cell/2+2,cy-cell/2+2,cell-4,cell-4);
                    drawRobot(g,cx,cy,Math.max(5,cell/3),s.body,s.direction,color);
                }
            }
            g.dispose();
        }
    }
}
