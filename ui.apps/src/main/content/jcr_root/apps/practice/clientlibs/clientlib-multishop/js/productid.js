






document.addEventListener('DOMContentLoaded', function() {
    // Get product ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    
    if (productId) {
        // Fetch specific product details from the API
        fetch(`https://fakestoreapi.com/products/${productId}`)
            .then(response => response.json())
            .then(product => {
                // Update product details on the page
                updateProductDetails(product);
            })
            .catch(error => {
                console.error('Error fetching product details:', error);
                document.querySelector('.container-fluid.pb-5').innerHTML = 
                    '<div class="text-center py-5"><h3>Failed to load product details</h3></div>';
            });
    } else {
        // No product ID provided
        document.querySelector('.container-fluid.pb-5').innerHTML = 
            '<div class="text-center py-5"><h3>No product selected</h3></div>';
    }
});

function updateProductDetails(product) {
    // Update product name
    document.querySelector('.h-100.bg-light.p-30 h3:first-child').textContent = product.title;
    
    // Update price
    document.querySelector('.font-weight-semi-bold.mb-4').textContent = `$${product.price}`;
    
    // Update description
    document.querySelector('.h-100.bg-light.p-30 p.mb-4').textContent = product.description;
    
    // Update main image (first carousel item)
    document.querySelector('.carousel-item.active img').src = product.image;
    
    // Update review count
    document.querySelector('.d-flex.mb-3 small.pt-1').textContent = `(${product.rating.count} Reviews)`;
    
    // Update star rating
    updateStarRating(product.rating.rate);
    
    // Update tab content if needed
    document.querySelector('#tab-pane-1 h4.mb-3').textContent = `${product.title} Description`;
    
    // Update review section
    document.querySelector('#tab-pane-3 h4.mb-4').textContent = 
        `${product.rating.count} review${product.rating.count !== 1 ? 's' : ''} for "${product.title}"`;
}

function updateStarRating(rating) {
    const ratingContainer = document.querySelector('.d-flex.mb-3 .text-primary');
    ratingContainer.innerHTML = '';
    
    // Add full stars
    const fullStars = Math.floor(rating);
    for (let i = 0; i < fullStars; i++) {
        const star = document.createElement('small');
        star.className = 'fas fa-star';
        ratingContainer.appendChild(star);
    }
    
    // Add half star if needed
    if (rating % 1 >= 0.5) {
        const halfStar = document.createElement('small');
        halfStar.className = 'fas fa-star-half-alt';
        ratingContainer.appendChild(halfStar);
    }
    
    // Add empty stars
    const emptyStars = 5 - Math.ceil(rating);
    for (let i = 0; i < emptyStars; i++) {
        const emptyStar = document.createElement('small');
        emptyStar.className = 'far fa-star';
        ratingContainer.appendChild(emptyStar);
    }
}