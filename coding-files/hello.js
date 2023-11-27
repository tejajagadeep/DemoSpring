// Create an async function getPost(url)
async function getPost(url) {
  // use try-catch block
  try {
    // use promise to call fetch(url) to get the response
    const response = await fetch(url);

    // Check if response is ok, if not throw error with message `Failed to fetch data. Status: ${response.status}`
    if (!response.ok) {
      throw new Error(`Failed to fetch data. Status: ${response.status}`);
    }

    // return data as json
    return response.json();
  } catch (error) {
    // catch error and throw error with message `Error fetching data: ${error.message}`
    throw new Error(`Error fetching data: ${error.message}`);
  }
}

module.exports = getPost;
// Example usage in another file

const getPost = require('./apiCall.js');

const apiUrl = 'https://jsonplaceholder.typicode.com/posts/1';

getPost(apiUrl)
  .then(data => {
    console.log('Data:', data);
  })
  .catch(error => {
    console.error(error.message);
  });
