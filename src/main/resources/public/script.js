window.onload = async (event) => {
    let dropdown = document.getElementById('country');

    const url = '/api/v1/countries/list';

    await fetch(url)
        .then(res => res.json())
        .then(payload => {
            let option;
            for (let i = 0; i < payload.data.length; i++) {
                option = document.createElement('option');
                option.text = payload.data[i];
                option.value = payload.data[i];
                dropdown.add(option);
                if (payload.data[i] == "India") {
                    dropdown.selectedIndex = i;
                }
            }
        });
}


async function timeSubmit(e) {
    e.preventDefault();
    const city = document.getElementById("city").value;
    const country = document.getElementById("country").value;
    document.getElementById("spinner").style.visibility = "visible";

    await fetch('/api/v1/time?' + new URLSearchParams({
            city,
            country,
        }))
        .then(res => res.json())
        .then()
        .then(res => document.getElementById("tzresult").innerHTML = buildTzTable(res.data))
        .then(x =>
            setTimeout(() => document.getElementById("spinner").style.visibility = "hidden", 250));

    return false;
}


function buildTzTable(data) {
    if (!data) {
        return `<table style='border: 1px solid #4b4b4b; width: 100%;'>
    <tr>
    <td style='border: 1px solid #4b4b4b; padding: 5px;'><b>Error</b></td>
    <td style='border: 1px solid #4b4b4b; padding: 5px;'>Not Found</td>
    </tr>
    </table>`;

    }
    let tbl = "<table style='border: 1px solid #4b4b4b; width: 100%;'>";
    let keysToShow = {
        countryCode: "Country Code",
        countryName: "Name of Country",
        zoneName: "Timezone",
        abbreviation: "Abbreviated Timezone",
        formatted: "Current Time"
    }

    for (let k in data) {
        if (k in keysToShow) {
            tbl += `<tr>`
            tbl += `<td style='border: 1px solid #4b4b4b; padding: 5px;'><b>${keysToShow[k]}</b></td>`;
            tbl += `<td style='border: 1px solid #4b4b4b; padding: 5px;'>${data[k]}</td>`;
            tbl += `</tr>`
        }
    }
    tbl += "</table>"
    return tbl;
}