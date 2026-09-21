const shortenButton = document.getElementById("shortenButton");

shortenButton.addEventListener("click", async function () {

    const originalUrl = document.getElementById("originalUrl").value;
    const result = document.getElementById("result");

    if (originalUrl.trim() === "") {
        result.innerText = "Please enter a URL.";
        return;
    }

    // Disable button while request is running
    shortenButton.disabled = true;
    shortenButton.innerText = "Shortening URL...";
    result.innerText = "";

    try {

        const response = await fetch("http://localhost:8080/api/urls", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                originalUrl: originalUrl
            })

        });

        const data = await response.json();

        if (!response.ok) {
            result.innerText = data.message || "Something went wrong.";
            return;
        }

        result.innerHTML = `
            <p>Your short URL:</p>

            <a href="${data.shortUrl}" target="_blank">
                ${data.shortUrl}
            </a>

            <br><br>

            <button id="copyButton">
                Copy URL
            </button>

            <p id="copyMessage"></p>
        `;

        const copyButton = document.getElementById("copyButton");

        copyButton.addEventListener("click", async function () {

            try {

                await navigator.clipboard.writeText(data.shortUrl);

                document.getElementById("copyMessage").innerText =
                    "URL copied successfully!";

            } catch (error) {

                document.getElementById("copyMessage").innerText =
                    "Unable to copy URL.";

                console.error(error);

            }

        });

    } catch (error) {

        result.innerText =
            "Unable to connect to the backend. Please start Spring Boot.";

        console.error(error);

    } finally {

        // Enable button after request finishes
        shortenButton.disabled = false;
        shortenButton.innerText = "Shorten URL";

    }

});