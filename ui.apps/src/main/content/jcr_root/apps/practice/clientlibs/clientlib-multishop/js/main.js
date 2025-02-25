$(document).ready(function() {
  // Listen for clicks on the shopping cart icon
  $('.fa-shopping-cart').click(function() {
      // Find the cart item count element
      var cartItemCount = $('.badge');
      
      // Check if the element is found
      if (cartItemCount.length > 0) {
          // Get the current item count (convert it to an integer)
          var currentCount = parseInt(cartItemCount.text(), 10);
          
          // Increment the item count
          var newCount = currentCount + 1;
          
          // Update the item count in the navigation bar
          cartItemCount.text(newCount);
      } else {
          console.log('Element not found');
      }
  });
});