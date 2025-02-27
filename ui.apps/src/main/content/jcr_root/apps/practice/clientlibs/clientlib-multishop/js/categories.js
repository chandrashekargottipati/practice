// Fetch products from the servlet
fetch('/bin/featured-products')
    .then(response => response.json())
    .then(products => {
        const productContainer = document.getElementById('product-container');
        
        // Ensure Font Awesome is loaded
        ensureFontAwesomeLoaded();
        
        // Loop through the products and create HTML for each product
        products.forEach(product => {
            const productHtml = `
                <div class="col-lg-3 col-md-4 col-sm-6 pb-1">
                    <div class="product-item bg-light mb-4">
                        <div class="product-img position-relative overflow-hidden">
                            <img class="img-fluid w-100" src="${product.image}" alt="${product.title}" />
                            <div class="product-action">
                                <a class="btn btn-outline-dark btn-square" href="/content/practice/us/en/shop-details.html?id=${product.id}">
                                    <span class="fa fa-shopping-cart"></span>
                                </a>
                                <a class="btn btn-outline-dark btn-square" href="#">
                                    <span class="far fa-heart"></span>
                                </a>
                                <a class="btn btn-outline-dark btn-square" href="#">
                                    <span class="fa fa-sync-alt"></span>
                                </a>
                                <a class="btn btn-outline-dark btn-square" href="/content/practice/us/en/shop-details.html?id=${product.id}">
                                    <span class="fa fa-search"></span>
                                </a>
                            </div>
                        </div>
                        <div class="text-center py-4">
                            <a class="h6 text-decoration-none text-truncate product-link" href="/content/practice/us/en/shop-details.html?id=${product.id}">${product.title}</a>
                            <div class="d-flex align-items-center justify-content-center mt-2">
                                <h5>$${product.price}</h5>
                                <h6 class="text-muted ml-2"><del>$${(product.price * 1.2).toFixed(2)}</del></h6>
                            </div>
                            <div class="d-flex align-items-center justify-content-center mb-1">
                                ${generateStarRating(product.rating.rate)}
                                <small>(${product.rating.count})</small>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            productContainer.insertAdjacentHTML('beforeend', productHtml);
        });

        // Add CSS and click handlers after products are loaded
        addProductStyles();
        addProductHoverAndClickEffects();
    })
    .catch(error => {
        console.error('Error fetching products:', error);
        document.getElementById('product-container').innerHTML = '<div class="col-12 text-center">Failed to load products.</div>';
    });

// Function to ensure Font Awesome is loaded
function ensureFontAwesomeLoaded() {
    const fontAwesomeLoaded = document.querySelector('link[href*="font-awesome"]') || 
                             document.querySelector('link[href*="fontawesome"]');
    
    if (!fontAwesomeLoaded) {
        const fontAwesomeLink = document.createElement('link');
        fontAwesomeLink.rel = 'stylesheet';
        fontAwesomeLink.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css';
        fontAwesomeLink.integrity = 'sha512-iBBXm8fW90+nuLcSKlbmrPcLa0OT92xO1BIsZ+ywDWZCvqsWgccV3gFoRBv0z+8dLJgyAHIhR35VZc2oM/gI1w==';
        fontAwesomeLink.crossOrigin = 'anonymous';
        document.head.appendChild(fontAwesomeLink);
        console.log('Font Awesome was added dynamically');
    }
}

// Function to generate star rating HTML
function generateStarRating(rating) {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 !== 0;
    const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
    let starsHtml = '';
    for (let i = 0; i < fullStars; i++) {
        starsHtml += '<small class="fas fa-star text-primary mr-1"></small>';
    }
    if (halfStar) {
        starsHtml += '<small class="fas fa-star-half-alt text-primary mr-1"></small>';
    }
    for (let i = 0; i < emptyStars; i++) {
        starsHtml += '<small class="far fa-star text-primary mr-1"></small>';
    }
    return starsHtml;
}

// Function to add CSS dynamically
function addProductStyles() {
    if (!document.getElementById('dynamic-product-styles')) {
        const style = document.createElement('style');
        style.id = 'dynamic-product-styles';
        style.innerHTML = `
            /* General container spacing */
            .container-fluid {
                padding: 20px;
            }

            /* Product grid adjustments */
            .row {
                display: flex;
                flex-wrap: wrap;
                justify-content: center;
            }

            /* Product Item */
            .product-item {
                border-radius: 10px;
                overflow: hidden;
                transition: all 0.3s ease-in-out;
            }

            .product-item:hover {
                box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
            }

            /* Product Image */
            .product-img {
                position: relative;
                width: 100%;
                height: 250px;
                overflow: hidden;
            }

            .product-img img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                transition: transform 0.3s ease;
            }

            .product-img:hover img {
                transform: scale(1.1);
            }

            /* Product Actions (buttons over image) */
            .product-action {
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                opacity: 0;
                transition: opacity 0.3s ease-in-out;
            }

            .product-img:hover .product-action {
                opacity: 1;
            }

            .product-action a {
                margin: 5px;
            }

            /* Product Details */
            .text-center {
                padding: 15px;
            }

            /* Product Title */
            .h6.text-decoration-none {
                font-size: 1rem;
                font-weight: 600;
                color: #333;
                display: block;
                text-overflow: ellipsis;
                overflow: hidden;
                white-space: nowrap;
            }

            .h6.text-decoration-none:hover {
                color: #007bff;
            }
        `;
        document.head.appendChild(style);
    }
}

// Function to add hover and click effects
function addProductHoverAndClickEffects() {
    const productItems = document.querySelectorAll('.product-item');
    productItems.forEach(item => {
        item.addEventListener('click', (e) => {
            const productLink = item.querySelector('.product-link');
            if (productLink && !e.target.closest('a')) {
                window.location.href = productLink.href;
            }
        });
    });

    setTimeout(() => {
        const icons = document.querySelectorAll('.product-action a span');
        let iconsVisible = true;
        
        icons.forEach(icon => {
            if (icon.offsetWidth === 0 || icon.offsetHeight === 0) {
                iconsVisible = false;
            }
        });

        if (!iconsVisible) {
            console.warn('Icons may not be displaying correctly. Check Font Awesome loading.');
        }
    }, 1000);
}
