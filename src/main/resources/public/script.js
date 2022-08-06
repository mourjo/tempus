let dropdown = document.getElementById('country');
dropdown.length = 0;

let defaultOption = document.createElement('option');
defaultOption.text = 'Choose State/Province';

dropdown.add(defaultOption);
dropdown.selectedIndex = 0;

const url = '/oopsie';

fetch(url)
  .then(
    function(response) {
      if (response.status !== 200) {
        console.warn('Looks like there was a problem. Status Code: ' +
          response.status);
        return;
      }

      // Examine the text in the response
      response.json().then(function(data) {
        let option;

    	for (let i = 0; i < data.data.length; i++) {
          option = document.createElement('option');
      	  option.text = data.data[i];
      	  option.value = data.data[i];
      	  dropdown.add(option);
    	}
      });
    }
  )
  .catch(function(err) {
    console.error('Fetch Error -', err);
  });


async function timeSubmit(e) {
      e.preventDefault();
      const city = document.getElementById("city").value;
      const country = document.getElementById("country").value;
      console.log(`Got ${city} and ${country}`)
      await fetch('/time?' + new URLSearchParams({
          city,
          country,
      })).then(res => res.json()).then(res => console.log(`I got ${JSON.stringify(res)}`));
      return false;
}
