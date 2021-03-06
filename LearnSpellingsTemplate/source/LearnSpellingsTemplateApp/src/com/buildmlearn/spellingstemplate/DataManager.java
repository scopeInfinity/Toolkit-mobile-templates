/* Copyright (c) 2012, BuildmLearn Contributors listed at http://buildmlearn.org/people/
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

 * Neither the name of the BuildmLearn nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.buildmlearn.spellingstemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class DataManager {
	private static DataManager instance = null;
	private String mTitle = null;
	private String mAuthor = null;
	private BufferedReader br;
	private ArrayList<WordModel> mList = null;
	private int countIndex = 0;
	private int countCorrect = 0;
	private int countWrong = 0;

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	public void readContent(Context myContext) {
		reset();
		try {
			br = new BufferedReader(new InputStreamReader(myContext.getAssets()
					.open("spelling_content.txt"))); // throwing a
														// FileNotFoundException?
			mTitle = br.readLine();
			mAuthor = br.readLine();
			String text;
			while ((text = br.readLine()) != null) {
				if (text.contains("==")) {
					String[] spelling = text.split("==");
					int startIndex = spelling[0].length() + 2;
					String des = text.substring(startIndex);
					mList.add(new WordModel(spelling[0], des));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close(); // stop reading
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void readXmlContent(Context myContext, String fileName) {
		XmlPullParserFactory factory;
		XmlPullParser parser;
		InputStreamReader is;
		try {
			factory = XmlPullParserFactory.newInstance();
			// .setNamespaceAware(true);
			factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

			parser = factory.newPullParser();

			is = new InputStreamReader(myContext.getAssets().open(fileName));

			parser.setInput(is);
			int eventType = parser.getEventType();
			WordModel app = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					mList = new ArrayList<WordModel>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equalsIgnoreCase("title")) {
						mTitle = parser.nextText();
					} else if (name.equalsIgnoreCase("author")) {
						mAuthor = parser.nextText();
					} else if (name.equalsIgnoreCase("item")) {
						app = new WordModel();
					} else if (app != null) {
						if (name.equalsIgnoreCase("word")) {
							app.setWord(parser.nextText());
						} else if (name.equalsIgnoreCase("meaning")) {
							app.setDescription(parser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("item") && app != null) {
						mList.add(app);
						// totalCards = model.size();
					}
				}
				eventType = parser.next();

			}
		} catch (XmlPullParserException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return model;
		// BuildmLearnModel.getInstance(myContext).setAllAppsList(model);

	}

	public void readXml(Context myContext, String fileName) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setValidating(false);

		DocumentBuilder db;
		Document doc;
		try {
			mList = new ArrayList<WordModel>();
			db = dbf.newDocumentBuilder();
			doc = db.parse(myContext.getAssets().open(fileName));
			doc.normalize();
			mTitle = doc.getElementsByTagName("title").item(0).getChildNodes()
					.item(0).getNodeValue();
			mAuthor = doc.getElementsByTagName("name").item(0).getChildNodes()
					.item(0).getNodeValue();
			NodeList childNodes = doc.getElementsByTagName("item");
			for (int i = 0; i < childNodes.getLength(); i++) {
				WordModel app = new WordModel();

				Node child = childNodes.item(i);

				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element element2 = (Element) child;

					app.setWord(getValue("word", element2));
					app.setDescription(getValue("meaning", element2));

				}
				mList.add(app);

			}
		} catch (ParserConfigurationException e) {
			Log.e("tag", e.getLocalizedMessage());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			Log.e("tag", e.getLocalizedMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.e("tag", e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("tag", e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private static String getValue(String tag, Element element) {
		NodeList nodeList = null;
		NodeList node1 = element.getElementsByTagName(tag);
		if (node1 != null && node1.getLength() != 0)
			nodeList = node1.item(0).getChildNodes();
		if (nodeList == null)
			return "";
		else if (nodeList.getLength() == 0)
			return "";
		else {
			Node node = (Node) nodeList.item(0);

			return node.getNodeValue();
		}
	}

	public ArrayList<WordModel> getList() {

		return mList;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public int getActiveWordCount() {
		return countIndex;

	}

	public void increaseCount() {
		countIndex++;
	}

	public void incrementCorrect() {
		countCorrect++;
	}

	public int getCorrect() {
		return countCorrect;
	}

	public int getWrong() {
		return countWrong;
	}

	public void incrementWrong() {
		countWrong++;
	}

	public void reset() {
		countCorrect = 0;
		mList.clear();
		countIndex = 0;
		countWrong = 0;
	}

}
