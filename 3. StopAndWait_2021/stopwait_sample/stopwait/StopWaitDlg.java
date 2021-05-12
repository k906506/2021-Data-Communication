
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jnetpcap.PcapIf;

public class StopWaitDlg extends JFrame implements BaseLayer {

	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	BaseLayer UnderLayer;

	private static LayerManager m_LayerMgr = new LayerManager();

	private JTextField ChattingWrite;

	Container contentPane;

	JTextArea ChattingArea; //梨쀭똿�솕硫� 蹂댁뿬二쇰뒗 �쐞移�
	JTextArea srcMacAddress;
	JTextArea dstMacAddress;

	JLabel lblsrc;  // Label(�씠由�)
	JLabel lbldst;

	JButton Setting_Button; //Port踰덊샇(二쇱냼)瑜� �엯�젰諛쏆� �썑 �셿猷뚮쾭�듉�꽕�젙
	JButton Chat_send_Button; //梨꾪똿�솕硫댁쓽 梨꾪똿 �엯�젰 �셿猷� �썑 data Send踰꾪듉

	static JComboBox<String> NICComboBox;

	int adapterNumber = 0;

	String Text;

	public static void main(String[] args) {
		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ChatAppLayer("ChatApp"));
		m_LayerMgr.AddLayer(new StopWaitDlg("GUI"));

		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ChatApp ( *GUI ) ) )");
	}

	public StopWaitDlg(String pName) {
		pLayerName = pName;

		setTitle("Stop & Wait Protocol");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(250, 250, 644, 425);
		contentPane = new JPanel();
		((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel chattingPanel = new JPanel();// chatting panel
		chattingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "chatting",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		chattingPanel.setBounds(10, 5, 360, 276);
		contentPane.add(chattingPanel);
		chattingPanel.setLayout(null);

		JPanel chattingEditorPanel = new JPanel();// chatting write panel
		chattingEditorPanel.setBounds(10, 15, 340, 210);
		chattingPanel.add(chattingEditorPanel);
		chattingEditorPanel.setLayout(null);

		ChattingArea = new JTextArea();
		ChattingArea.setEditable(false);
		ChattingArea.setBounds(0, 0, 340, 210);
		chattingEditorPanel.add(ChattingArea);// chatting edit

		JPanel chattingInputPanel = new JPanel();// chatting write panel
		chattingInputPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		chattingInputPanel.setBounds(10, 230, 250, 20);
		chattingPanel.add(chattingInputPanel);
		chattingInputPanel.setLayout(null);

		ChattingWrite = new JTextField();
		ChattingWrite.setBounds(2, 2, 250, 20);// 249
		chattingInputPanel.add(ChattingWrite);
		ChattingWrite.setColumns(10);// writing area

		JPanel settingPanel = new JPanel(); //Setting 愿��젴 �뙣�꼸
		settingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "setting",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		settingPanel.setBounds(380, 5, 236, 371);
		contentPane.add(settingPanel);
		settingPanel.setLayout(null);

		JPanel sourceAddressPanel = new JPanel();
		sourceAddressPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sourceAddressPanel.setBounds(10, 140, 170, 20);
		settingPanel.add(sourceAddressPanel);
		sourceAddressPanel.setLayout(null);

		lblsrc = new JLabel("Source Mac Address");
		lblsrc.setBounds(10, 115, 170, 20); //�쐞移� 吏��젙
		settingPanel.add(lblsrc); //panel 異붽�

		srcMacAddress = new JTextArea();
		srcMacAddress.setBounds(2, 2, 170, 20); 
		sourceAddressPanel.add(srcMacAddress);// src address

		JPanel destinationAddressPanel = new JPanel();
		destinationAddressPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		destinationAddressPanel.setBounds(10, 212, 170, 20);
		settingPanel.add(destinationAddressPanel);
		destinationAddressPanel.setLayout(null);

		lbldst = new JLabel("Destination Mac Address");
		lbldst.setBounds(10, 187, 190, 20);
		settingPanel.add(lbldst);

		dstMacAddress = new JTextArea();
		dstMacAddress.setBounds(2, 2, 170, 20);
		destinationAddressPanel.add(dstMacAddress);// dst address

		JLabel NICLabel = new JLabel("NIC List");
		NICLabel.setBounds(10, 20, 170, 20);
		settingPanel.add(NICLabel);

		NICComboBox = new JComboBox();
		NICComboBox.setBounds(10, 49, 170, 20);
		settingPanel.add(NICComboBox);
		
		
		NILayer tempNiLayer = (NILayer) m_LayerMgr.GetLayer("NI"); //肄ㅻ낫諛뺤뒪 由ъ뒪�듃�뿉 異붽��븯湲� �쐞�븳 �씤�꽣�럹�씠�뒪 媛앹껜

		for (int i = 0; i < tempNiLayer.getAdapterList().size(); i++) { //�꽕�듃�썙�겕 �씤�꽣�럹�씠�뒪媛� ���옣�맂 �뼱�럞�꽣 由ъ뒪�듃�쓽 �궗�씠利덈쭔�겮�쓽 諛곗뿴 �깮�꽦
			//NICComboBox.addItem(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(i).getDescription());
			PcapIf pcapIf = tempNiLayer.GetAdapterObject(i); //
			NICComboBox.addItem(pcapIf.getName()); // NIC �꽑�깮 李쎌뿉 �뼱�뙌�꽣瑜� 蹂댁뿬以�
		}

		NICComboBox.addActionListener(new ActionListener() { //combo諛뺤뒪瑜� �닃���쓣 �븣�쓽 �룞�옉

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//adapterNumber = NICComboBox.getSelectedIndex();
				JComboBox jcombo = (JComboBox) e.getSource();
				adapterNumber = jcombo.getSelectedIndex();
				System.out.println("Index: " + adapterNumber); 
				try {
					srcMacAddress.setText("");
					srcMacAddress.append(get_MacAddress(((NILayer) m_LayerMgr.GetLayer("NI"))
							.GetAdapterObject(adapterNumber).getHardwareAddress()));

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		try {// ���젅濡� MAC二쇱냼 蹂댁씠寃뚰븯湲�
			srcMacAddress.append(get_MacAddress(
					((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(adapterNumber).getHardwareAddress()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		;

		Setting_Button = new JButton("Setting");// setting
		Setting_Button.setBounds(80, 270, 100, 20);
		Setting_Button.addActionListener(new setAddressListener());
		settingPanel.add(Setting_Button);// setting

		Chat_send_Button = new JButton("Send");
		Chat_send_Button.setBounds(270, 230, 80, 20);
		Chat_send_Button.addActionListener(new setAddressListener());
		chattingPanel.add(Chat_send_Button);// chatting send button

		setVisible(true);

	}

	class setAddressListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == Setting_Button) { //setting 踰꾪듉 �늻瑜� �떆

				if (Setting_Button.getText() == "Reset") { //reset �닃�젮議뚯쓣 寃쎌슦,
					srcMacAddress.setText("");  //二쇱냼 怨듬갚�쑝濡� 諛붾��
					dstMacAddress.setText("");  //二쇱냼 怨듬갚�쑝濡� 諛붾��
					Setting_Button.setText("Setting"); //踰꾪듉�쓣 �늻瑜대㈃, setting�쑝濡� 諛붾��
					srcMacAddress.setEnabled(true);  //踰꾪듉�쓣 �솢�꽦�솕�떆�궡
					dstMacAddress.setEnabled(true);  //踰꾪듉�쓣 �솢�꽦�솕�떆�궡
				}  
				else { //�넚�닔�떊二쇱냼 �꽕�젙
					 
					byte[] srcAddress = new byte[6];
					byte[] dstAddress = new byte[6];

					String src = srcMacAddress.getText(); //MAC 二쇱냼瑜� String byte濡� 蹂��솚
					String dst = dstMacAddress.getText();

					String[] byte_src = src.split("-"); //Sting MAC 二쇱냼瑜�"-"濡� �굹�닎
					for (int i = 0; i < 6; i++) {
						srcAddress[i] = (byte) Integer.parseInt(byte_src[i], 16); //16鍮꾪듃 (2byte)
					}

					String[] byte_dst = dst.split("-");//Sting MAC 二쇱냼瑜�"-"濡� �굹�닎
					for (int i = 0; i < 6; i++) {
						dstAddress[i] = (byte) Integer.parseInt(byte_dst[i], 16);//16鍮꾪듃 (2byte)
					}

					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetSrcAddress(srcAddress); //�씠遺�遺꾩쓣 �넻�빐 �꽑�깮�븳 二쇱냼瑜� �봽濡쒓렇�옩 �긽 �냼�뒪二쇱냼濡� �궗�슜媛��뒫
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetDstAddress(dstAddress); //�씠遺�遺꾩쓣 �넻�빐 �꽑�깮�븳 二쇱냼瑜� �봽濡쒓렇�옩 �긽 紐⑹쟻吏�二쇱냼濡� �궗�슜媛��뒫

					((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(adapterNumber);

					Setting_Button.setText("Reset"); //setting 踰꾪듉 �늻瑜대㈃ 由ъ뀑�쑝濡� 諛붾��
					dstMacAddress.setEnabled(false);  //踰꾪듉�쓣 鍮꾪솢�꽦�솕�떆�궡
					srcMacAddress.setEnabled(false);  //踰꾪듉�쓣 鍮꾪솢�꽦�솕�떆�궡  
				} 
			}

			if (e.getSource() == Chat_send_Button) { //send 踰꾪듉 �늻瑜대㈃, 
				if (Setting_Button.getText() == "Reset") { 
					String input = ChattingWrite.getText(); //梨꾪똿李쎌뿉 �엯�젰�맂 �뀓�뒪�듃瑜� ���옣
					ChattingArea.append("[SEND] : " + input + "\n"); //�꽦怨듯븯硫� �엯�젰媛� 異쒕젰
					byte[] bytes = input.getBytes(); //�엯�젰�맂 硫붿떆吏�瑜� 諛붿씠�듃濡� ���옣
					
					((ChatAppLayer)m_LayerMgr.GetLayer("ChatApp")).Send(bytes, bytes.length);
					//梨꾪똿李쎌뿉 �엯�젰�맂 硫붿떆吏�瑜� chatApplayer濡� 蹂대깂
					ChattingWrite.setText(""); 
					//梨꾪똿 �엯�젰�� �떎�떆 鍮꾩썙以�
				} else {
					JOptionPane.showMessageDialog(null, "Address Setting Error!.");//二쇱냼�꽕�젙 �뿉�윭
				}
			}
		}
	}

	public String get_MacAddress(byte[] byte_MacAddress) { //MAC Byte二쇱냼瑜� String�쑝濡� 蹂��솚

		String MacAddress = "";
		for (int i = 0; i < 6; i++) { 
			//2�옄由� 16吏꾩닔瑜� ��臾몄옄濡�, 洹몃━怨� 1�옄由� 16吏꾩닔�뒗 �븵�뿉 0�쓣 遺숈엫.
			MacAddress += String.format("%02X%s", byte_MacAddress[i], (i < MacAddress.length() - 1) ? "" : "");
			
			if (i != 5) {
				//2�옄由� 16吏꾩닔 �옄由� �떒�쐞 �뮘�뿉 "-"遺숈뿬二쇨린
				MacAddress += "-";
			}
		} 
		System.out.println("mac_address:" + MacAddress);
		return MacAddress;
	}

	public boolean Receive(byte[] input) { //硫붿떆吏� Receive
		if (input != null) {
			byte[] data = input;   //byte �떒�쐞�쓽 input data
			Text = new String(data); //�븘�옒痢듭뿉�꽌 �삱�씪�삩 硫붿떆吏�瑜� String text濡� 蹂��솚�빐以�
			ChattingArea.append("[RECV] : " + Text + "\n"); //梨꾪똿李쎌뿉 �닔�떊硫붿떆吏�瑜� 蹂댁뿬以�
			return false;
		}
		return false ;
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);

	}

}
