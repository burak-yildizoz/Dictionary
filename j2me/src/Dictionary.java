import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import java.util.*;
import java.lang.*;

import java.lang.String;
import java.lang.StringBuffer;
import java.io.Reader;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;

public class Dictionary extends MIDlet implements CommandListener
{
    private Turkce tr;
	private Form form_en, form_tr;
    private TextField wordbox_en, wordbox_tr;
    private Command submit_en, submit_tr, change_en, change_tr;
    private StringItem meanings_item_en, meanings_item_tr;
    private Display display;
    private boolean is_tr;

	public void startApp()
	{
        tr = new Turkce();
        try
        {
            tr.baslat();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		form_en = new Form("Dictionary");
        form_tr = new Form("S"+tr.o+"zl"+tr.u+"k");
        wordbox_en = new TextField("Word:", "", 30, TextField.ANY);
        wordbox_tr = new TextField("Kelime:", "", 30, TextField.ANY);
        form_en.append(wordbox_en);
        form_tr.append(wordbox_tr);
        submit_en = new Command("Submit", Command.OK, 2);
        submit_tr = new Command("G"+tr.o+"nder", Command.OK, 2);
        change_en = new Command("T"+tr.u+"rk"+tr.c+"e", Command.CANCEL, 2);
        change_tr = new Command("English", Command.CANCEL, 2);
        form_en.addCommand(submit_en);
        form_tr.addCommand(submit_tr);
        form_en.addCommand(change_en);
        form_tr.addCommand(change_tr);
        form_en.setCommandListener(this);
        form_tr.setCommandListener(this);
        meanings_item_en = new StringItem("Meanings:", "");
        meanings_item_tr = new StringItem("Anlam"+tr.i+":", "");
        form_en.append(meanings_item_en);
        form_tr.append(meanings_item_tr);
        display = Display.getDisplay(this);
        is_tr = false;
        show_form();
	}

	public void pauseApp() {}

	public void destroyApp(boolean unconditional) {}

    public void commandAction(Command c, Displayable d)
    {
        if (is_tr)
        {
            if (c == submit_tr)
            {
                String[] meanings = new String[0];
                String[] similars = new String[0];
                String word = wordbox_tr.getString();
                try
                {
                    meanings = get_meanings(word, is_tr);
                    similars = get_words_with_initials(word, is_tr);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                String text = arr2str(meanings) + "\n" + word + " ile ba"+tr.s+"layan kelimeler:\n" + arr2str(similars);
                meanings_item_tr.setText(text);
            }
        }
        else
        {
            if (c == submit_en)
            {
                String[] meanings = new String[0];
                String[] similars = new String[0];
                String word = wordbox_en.getString();
                try
                {
                    meanings = get_meanings(word, is_tr);
                    similars = get_words_with_initials(word, is_tr);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                String text = arr2str(meanings) + "\nWords that start with " + word + ":\n" + arr2str(similars);
                meanings_item_en.setText(text);
            }
        }
        if ((c == change_en) || (c == change_tr))
        {
            is_tr = !is_tr;
            show_form();
        }
    }

    private void show_form()
    {
        if (is_tr)
        {
            wordbox_tr.setString(wordbox_en.getString());
            meanings_item_tr.setText("");
            display.setCurrent(form_tr);
        }
        else
        {
            wordbox_en.setString(wordbox_tr.getString());
            meanings_item_en.setText("");
            display.setCurrent(form_en);
        }
    }

    private String arr2str(String[] str_arr)
    {
        String str = "";
        for (int i = 0; i < str_arr.length; i++)
        {
            str += str_arr[i] + "\n";
        }
        return str;
    }

    private boolean skip_line(Reader in) throws IOException
    {
        boolean is_read = false;
        int read = 0;
        while (read != -1)
        {
            read = in.read();
            if (read == '\n')
            {
                is_read = true;
                break;
            }
        }
        return is_read;
    }

    private String get_word(Reader in) throws IOException
    {
        String word = "";
        StringBuffer sb = new StringBuffer(1024);
        int read;
        boolean stop = false;
        while (!stop)
        {
            read = in.read();
            switch (read)
            {
            case '\n':
            case '\t':
            case -1:
                word = sb.toString();
                stop = true;
                break;
            default:
                sb.append((char)read);
            }
        }
        return word;
    }

    private String[] get_words(Reader in) throws IOException
    {
        Vector words_vec = new Vector();
        StringBuffer sb = new StringBuffer(1024);
        int read;
        boolean stop = false;
        while (!stop)
        {
            read = in.read();
            switch (read)
            {
            case '\n':
                stop = true;
            case '\t':
                words_vec.addElement(sb.toString());
                sb = new StringBuffer(1024);
                break;
            case -1:
                stop = true;
                break;
            default:
                sb.append((char)read);
            }
        }
        String[] words = new String[words_vec.size()];
        for (int i = 0; i < words_vec.size(); i++)
        {
            words[i] = (String)words_vec.elementAt(i);
        }
        return words;
    }

    public String[] get_words_with_initials(String initials, boolean is_tr) throws Exception
    {
        String content[] = new String[0];
        if (initials.length() < 2)
        {
            return content;
        }
        String filename = (is_tr ? "tr/" : "en/") + initials.charAt(0) + "/" + initials.charAt(1) + ".txt";
        Reader in = new InputStreamReader(this.getClass().getResourceAsStream(filename), "UTF-8");
        Vector words_vec = new Vector();
        while (in.ready())
        {
            String word = get_word(in);
            if ((word.length() >= initials.length()) && initials.equals(word.substring(0, initials.length())))
            {
                words_vec.addElement(word);
            }
            else if (!skip_line(in))
            {
                break;
            }
        }
        content = new String[words_vec.size()];
        for (int i = 0; i < words_vec.size(); i++)
        {
            content[i] = (String)words_vec.elementAt(i);
        }
        return content;
    }

    public String[] get_meanings(String word, boolean is_tr) throws Exception
    {
        String content[] = new String[0];
        if (word.length() < 2)
        {
            return content;
        }
        String filename = (is_tr ? "tr/" : "en/") + word.charAt(0) + "/" + word.charAt(1) + ".txt";
        Reader in = new InputStreamReader(this.getClass().getResourceAsStream(filename), "UTF-8");
        while (in.ready())
        {
            if (word.equals(get_word(in)))
            {
                content = get_words(in);
                break;
            }
            else if (!skip_line(in))
            {
                break;
            }
        }
        return content;
    }

    // https://stackoverflow.com/questions/739691/reading-text-file-in-j2me/739744#739744
    public String readFromFile(String filename) throws Exception
    {
        String content = "";
        Reader in = new InputStreamReader(this.getClass().getResourceAsStream(filename), "UTF-8");
        StringBuffer temp = new StringBuffer(1024);
        char[] buffer = new char[1024];
        int read;
        while ((read = in.read(buffer, 0, buffer.length)) != -1)
        {
            temp.append(buffer, 0, read);
        }
        content = temp.toString();
        return content;
    }

    public static final class Turkce
	{
		public static final byte[] C_byte = new byte[] {(byte)0xc3, (byte)0x87};
		public static final byte[] G_byte = new byte[] {(byte)0xc4, (byte)0x9e};
		public static final byte[] I_byte = new byte[] {(byte)0xc4, (byte)0xb0};
		public static final byte[] O_byte = new byte[] {(byte)0xc3, (byte)0x96};
		public static final byte[] S_byte = new byte[] {(byte)0xc5, (byte)0x9e};
		public static final byte[] U_byte = new byte[] {(byte)0xc3, (byte)0x9c};
		public static final byte[] c_byte = new byte[] {(byte)0xc3, (byte)0xa7};
		public static final byte[] g_byte = new byte[] {(byte)0xc4, (byte)0x9f};
		public static final byte[] i_byte = new byte[] {(byte)0xc4, (byte)0xb1};
		public static final byte[] o_byte = new byte[] {(byte)0xc3, (byte)0xb6};
		public static final byte[] s_byte = new byte[] {(byte)0xc5, (byte)0x9f};
		public static final byte[] u_byte = new byte[] {(byte)0xc3, (byte)0xbc};

		public static String C = "C";
		public static String G = "G";
		public static String I = "I";
		public static String O = "O";
		public static String S = "S";
		public static String U = "U";
		public static String c = "c";
		public static String g = "g";
		public static String i = "i";
		public static String o = "o";
		public static String s = "s";
		public static String u = "u";

		public Turkce() {}

		public void baslat() throws UnsupportedEncodingException
		{
			C = new String(C_byte, "UTF-8");
			G = new String(G_byte, "UTF-8");
			I = new String(I_byte, "UTF-8");
			O = new String(O_byte, "UTF-8");
			S = new String(S_byte, "UTF-8");
			U = new String(U_byte, "UTF-8");
			c = new String(c_byte, "UTF-8");
			g = new String(g_byte, "UTF-8");
			i = new String(i_byte, "UTF-8");
			o = new String(o_byte, "UTF-8");
			s = new String(s_byte, "UTF-8");
			u = new String(u_byte, "UTF-8");
		}
	}
}
