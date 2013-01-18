package agent;


import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses XML content to return the values in the nodes
 * used to interact with the TCG java server pages returned
 * xml content
 * 
 *
 */

public class XMLParser {
	/** variables */
	/** Document Builder Factory object */
	private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	/** Document Builder object */
	private DocumentBuilder documentBuilder;
	/** Document object to parse XML content */
	private Document document;
	/** NodeList object to manage nodes */
	private NodeList nodeList;
	/** Node object to manage nodes */
	private Node node;
	/** Element object to manage elements */
	private Element element;
	/** JSON object to encode elements in JSON object */
	private JSONObject jsonObj = new JSONObject();
	
	/** constructor */
	/**
	 * Does nothing at this point in time, methods in this class are used to pass the 
	 * inputstream and begin the parsing of the XML content
	 */
	public XMLParser() {
		
	}
	
	/**
	 * Parses the stream for XML content and returns it format as a JSON Object
	 * for use in transmission to the Darkstar Server
	 * 
	 * @param inputStream	incoming data stream from the TCG server
	 * @return				returns JSON Object for transmission to Darkstar server
	 */

	public JSONObject parseStream(InputStream inputStream) {
		try {																							// try to parse the input stream
			documentBuilder = documentBuilderFactory.newDocumentBuilder();								// instantiate document builder
			document = documentBuilder.parse(inputStream);												// parse the input stream
			document.getDocumentElement().normalize();													// put elements into nodes
			/** for testing only */
			System.out.println("Root element :" + document.getDocumentElement().getNodeName());
			System.out.println("-----------------------");
			/** end for testing only */
			if (("playerData").equals(document.getDocumentElement().getNodeName())) {					// parse playerData data
				nodeList = document.getElementsByTagName("playerData");									// get elements for node list
				for (int i = 0; i < nodeList.getLength(); i++) {										// iterate through node list
					node = nodeList.item(i);															// get node
					if (node.getNodeType() == Node.ELEMENT_NODE) {										// make sure node type is element node
						element = (Element)node;														// cast node to element object
	  					jsonObj.put("userNickName", getXMLTagValue("userNickName", element));			// put userNickName to json object
	  					jsonObj.put("userScore", getXMLTagValue("userScore", element));					// put userScore to json object		
	  					jsonObj.put("userPlayCounter", getXMLTagValue("userPlayCounter", element));		// put userPlayCounter to json object    				
	  					jsonObj.put("userWinCounter", getXMLTagValue("userWinCounter", element));		// put userWinCounter to json object
	  					jsonObj.put("userInterruptCounter", 											// put userInterruptCounter to json object
	  							getXMLTagValue("userInterruptCounter", element));	
	  					jsonObj.put("userRank", getXMLTagValue("userRank", element));					// put userRank to json object
				      
				      /** for testing only */
				      /*System.out.println("userNickName : " + getXMLTagValue("userNickName", element));
				      System.out.println("userScore : " + getXMLTagValue("userScore", element));
				      System.out.println("userPlayCounter : " + getXMLTagValue("userPlayCounter", element));
				      System.out.println("userWinCounter : " + getXMLTagValue("userWinCounter", element));
				      System.out.println("userInterruptCounter : " + getXMLTagValue("userInterruptCounter", element));
				      System.out.println("userRank : " + getXMLTagValue("userRank", element));
				      /** end for testing only */
				   }
				}
			} else if (("deck").equals(document.getDocumentElement().getNodeName())) {					// parse deck data
				nodeList = document.getElementsByTagName("card");										// get elements for node list
				for (int i = 0; i < nodeList.getLength(); i++) {										// iterate through node list
					node = nodeList.item(i);															// get node
					if (node.getNodeType() == Node.ELEMENT_NODE) {										// make sure node type is element node
						element = (Element)node;														// cast node to element object
						JSONObject jsonArrayNest = new JSONObject();									// create temp json obj to handle nested values
						jsonArrayNest.put("userCardId", element.getAttribute("userCardID"));			// put usercardid to JSON object
						jsonArrayNest.put("cardId", element.getAttribute("cardID"));					// put cardid to JSON object
						jsonArrayNest.put("cardType", element.getAttribute("cardType"));				// put cardType to JSON object
						jsonArrayNest.put("cardStrength", element.getAttribute("cardStrength"));		// put cardStrength to JSON object
						jsonArrayNest.put("cardName", getXMLTagValue("cardName", element));				// put cardName to JSON object	
						jsonArrayNest.put("cardPath", getXMLTagValue("cardPath", element));				// put cardPath to JSON object		
						jsonArrayNest.put("cardInfo", getXMLTagValue("cardInfo", element));				// put cardInfo to JSON object
						/** for testing only */
					      System.out.println("userCardId : " + element.getAttribute("userCardID"));
					      System.out.println("cardId : " + element.getAttribute("cardID"));
					      System.out.println("cardType : " + element.getAttribute("cardType"));					
					      System.out.println("cardName : " + getXMLTagValue("cardName", element));			
	  					if (("A").equals(element.getAttribute("cardType"))) {							// parse for Avatar cards
	  						jsonArrayNest.put("mhp", getXMLTagValue("mhp", element));					// put mhp to JSON object
	  						jsonArrayNest.put("atk", getXMLTagValue("atk", element));					// put atk to JSON object
	  						jsonArrayNest.put("def", getXMLTagValue("def", element));					// put def to JSON object
	  						jsonArrayNest.put("rng", getXMLTagValue("rng", element));					// put rng to JSON object
	  						jsonArrayNest.put("CLASS", getXMLTagValue("CLASS", element));				// put CLASS to JSON object
	  						jsonArrayNest.put("element", getXMLTagValue("element", element));			// put element to JSON object
	  						jsonArrayNest.put("size", getXMLTagValue("size", element));					// put size to JSON object
						} else if (("T").equals(element.getAttribute("cardType")) ||
  							("M").equals(element.getAttribute("cardType"))) {							// parse for Tool/Magic cards
							jsonArrayNest.put(															// put effectInterval to JSON object
								"effectInterval", getXMLTagValue("effectInterval", element));	
	  					}
	  					jsonObj.accumulate("cards", jsonArrayNest);										// put json array to json obj

					}
				}
			} else if (("deckNameInfo").equals(document.getDocumentElement().getNodeName())) {			// parse deck data
				nodeList = document.getElementsByTagName("deckName");									// get elements for node list
				for (int i = 0; i < nodeList.getLength(); i++) {										// iterate through node list
					node = nodeList.item(i);															// get node
					if (node.getNodeType() == Node.ELEMENT_NODE) {										// make sure node type is element node
						element = (Element)node;														// cast node to element object
						jsonObj.put("deckId", element.getAttribute("id"));								// put deck id
						jsonObj.put("deckName", node.getTextContent());									// put deck name
					System.out.println("deckId : " + element.getAttribute("id"));						
					System.out.println("deckName: " + node.getTextContent());
					}
				}
			}
			/**
			 * Agents will use only one deck, no need to build multiple decks since they can autocreate
			 * decks at start up very quickly, if more than one deck required, the use similar method to
			 * one above used for cards in a deck
			 */	
			
			return jsonObj;																				// return jsonObj
		} catch (Exception e) {																			// catch exceptions
			e.printStackTrace();																		// print stack trace
			return jsonObj;																				// return jsonObj
		}
	}

		
	/**
	 * Gets the value of the XML tag element specified by the passed
	 * string tag 
	 * 
	 * @param tag		tag name for the element
	 * @param element	element to get xml value from
	 * @return			node value for the specified XML tag
	 */
	private String getXMLTagValue(String tag, Element element) {
		NodeList tempNodeList = element.getElementsByTagName(tag).item(0).getChildNodes();				// create temp node list with children
		Node tempNode = (Node)tempNodeList.item(0);														// create temp node from node list
	    return tempNode.getNodeValue();																	// return node value
	  }
}