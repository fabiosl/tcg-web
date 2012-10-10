function editDeck() {
	var deck_id = document.getElementById('deck_select').selectedIndex;
	window.location = "/edit_deck/" + deck_id + "/";
}

function createDeck() {
	window.location = "/create_deck/";
}

function getRemoveOnClick(elem) {
	var card = elem.closest('.card');
	var clone = card.clone();
	clone.children("div.data").children("#icon").attr("src", "/static/img/add.png");
	clone.children("div.data").children("#icon").attr("onclick", "getAddOnClick($(this))");
	clone.hide();
	card.hide("puff").delay(1).queue(function() {
		card.remove();
		$('#allcardsplace').prepend(clone);
		clone.show("puff")
		updateDeckCardsCounter()
	});
}

function getAddOnClick(elem) {
	var card = elem.closest('.card');
	var clone = card.clone();
	clone.children("div.data").children("#icon").attr("src", "/static/img/remove.png");
	clone.children("div.data").children("#icon").attr("onclick", "getRemoveOnClick($(this))");
	clone.hide();
	card.hide("puff").delay(1).queue(function() {
		card.remove();
		$('#deckcardsplace').prepend(clone);
		clone.show("puff")
		clone.attr("style","something"); //Bug-Fix that makes no sense. If you remove this line, when you try to see the fancybox of a card that was removed, it redirects you to the page.
		updateDeckCardsCounter();
	});
	
	
}

function updateSaveDeckButton(numCards) {
	if(numCards == 30) {
		$("#save_deck_button").attr("disabled", false);
		$("#save_deck_button").text("Save");
	} else {
		$("#save_deck_button").attr("disabled", true);
		$("#save_deck_button").text("Must complete the deck");
	}
}

function updateAddIcons(numCards) {
	if(numCards == 30) {
		$("#allcardsplace").children("div.card").children("div.data").children("#icon").attr("onclick", "");
		$("#allcardsplace").children("div.card").children("div.data").children("#icon").attr("src", "/static/img/add_disabled.png");
	} else {
		$("#allcardsplace").children("div.card").children("div.data").children("#icon").attr("onclick", "getAddOnClick($(this))");
		$("#allcardsplace").children("div.card").children("div.data").children("#icon").attr("src", "/static/img/add.png");
	}
}

function updateDeckCardsCounter() {
	var numCards = $('#deckcardsplace').children().size()
	$('#deck_cards_counter').text(numCards + "/30");
	updateSaveDeckButton(numCards);
	updateAddIcons(numCards);
	applyFancyBox();
	
}

function applyFancyBox(){
	$("a.iframe").fancybox(
	    	{
				'transitionIn'	:	'elastic',
				'transitionOut'	:	'elastic',
				'speedIn'		:	600, 
				'speedOut'		:	200, 
				'autoDimensions': false,
				'width'        	: 244,
				'height'		: 354
			}
				
		);

}

function init() {
	updateDeckCardsCounter();
	$("#save_deck_button").attr("disabled", true);
	$("#save_deck_button").text("Modify your deck before saving");
	$('#loadingDiv').hide();

	$('#loadingDiv').ajaxStart(function() {
		$(this).slideToggle();
	}).ajaxStop(function() {
		$(this).slideToggle();
	});
	
	applyFancyBox();
		
}

function saveDeck() {
	var cardString = "";
	var separator = "";
	$.each($('#deckcardsplace').children(), function(intIntex, child) {
		cardString += separator + child.getAttribute("user_card_id");
		separator = "*";
	});

	//Ajax POST to save_deck to be received on Django
	$.post("/save_deck", {
		cardString : cardString,
		deckName: $('#deck_cards_name').text()
	}, function(data) {
		if(data == "success") {
			alert("Deck Successfully saved.")
		}
	});
}

function makeDeckTitleEditable() {
	
	$("#deck_cards_name").keydown(function(event) {
		if(event.keyCode == 13) { //Enter pressed
			$('#deck_cards_name').attr('contenteditable', 'false')
			$('#deck_cards_name').blur();
			updateDeckCardsCounter()
		}
	});
	
	$('#deck_cards_name').attr('contenteditable', 'true')
	$('#deck_cards_name').focus();
}

function deleteDeck(){
	$.post("/delete_deck", {},
	 function(data) {
		if(data == "success") {
			alert("Deck Successfully deleted.")
			window.location="/"
		}
	});
}

document.ready = init;