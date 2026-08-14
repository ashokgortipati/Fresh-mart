package com.example.data.seed

import com.example.data.model.*

object SampleData {

    val sampleCoupons = listOf(
        Coupon(
            code = "FRESHMART50",
            title = "Flat $50 OFF",
            flatDiscount = 50.0,
            minOrderValue = 200.0,
            description = "Applicable on all orders above $200"
        ),
        Coupon(
            code = "SEAFOOD20",
            title = "20% OFF Seafood",
            discountPercent = 20,
            minOrderValue = 100.0,
            description = "Save 20% on all fresh catch and ready-to-cook items"
        ),
        Coupon(
            code = "TRYONSTYLE",
            title = "15% OFF Apparel",
            discountPercent = 15,
            minOrderValue = 80.0,
            description = "Special discount on all AI Virtual Try-On eligible fashion"
        )
    )

    val sampleDeliverySlots = listOf(
        DeliverySlot("slot_1", "Today", "07:00 AM - 09:00 AM (Early Express)", isSameDay = true, isAvailable = true, surcharge = 0.0),
        DeliverySlot("slot_2", "Today", "11:00 AM - 01:00 PM (Lunch Slot)", isSameDay = true, isAvailable = true, surcharge = 0.0),
        DeliverySlot("slot_3", "Today", "04:00 PM - 07:00 PM (Evening Fresh)", isSameDay = true, isAvailable = true, surcharge = 0.0),
        DeliverySlot("slot_4", "Today", "07:30 PM - 10:00 PM (Night Rush)", isSameDay = true, isAvailable = true, surcharge = 0.0),
        DeliverySlot("slot_5", "Tomorrow", "07:00 AM - 10:00 AM (Morning Catch)", isSameDay = false, isAvailable = true, surcharge = 0.0),
        DeliverySlot("slot_6", "Tomorrow", "02:00 PM - 05:00 PM (Afternoon Delivery)", isSameDay = false, isAvailable = true, surcharge = 0.0)
    )

    val sampleAddresses = listOf(
        Address(
            id = "addr_1",
            tag = "Home",
            recipientName = "Ashok Gortipati",
            phone = "+1 (555) 234-5678",
            streetAddress = "452 Ocean Avenue, Suite 12B",
            city = "San Francisco",
            state = "California",
            postalCode = "94107",
            isDefault = true
        ),
        Address(
            id = "addr_2",
            tag = "Work / Office",
            recipientName = "Ashok Gortipati",
            phone = "+1 (555) 234-9988",
            streetAddress = "100 Innovation Parkway, Floor 4",
            city = "San Francisco",
            state = "California",
            postalCode = "94103",
            isDefault = false
        )
    )

    val sampleProducts = listOf(
        // ================= CLOTHING =================
        Product(
            id = "cl_1",
            name = "Classic Oxford Slim-Fit Cotton Shirt",
            category = ProductCategory.CLOTHING,
            subCategory = "Shirts",
            description = "Tailored from 100% breathable organic long-staple cotton with a crisp button-down collar. Perfect for formal and smart-casual occasions.",
            price = 45.0,
            originalPrice = 65.0,
            rating = 4.9f,
            reviewCount = 312,
            unit = "piece",
            images = listOf(
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600",
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600"
            ),
            isDeal = true,
            isFeatured = true,
            isBestSeller = true,
            tryOnCompatible = true,
            availableSizes = listOf("S", "M", "L", "XL", "XXL"),
            availableColors = listOf("Sky Blue", "Pure White", "Navy Blue"),
            fabricOrIngredients = "100% Organic Egyptian Cotton (180 GSM)",
            tags = listOf("men", "shirt", "formal", "office", "try-on")
        ),
        Product(
            id = "cl_2",
            name = "Bohemian Floral Embroidered Maxi Dress",
            category = ProductCategory.CLOTHING,
            subCategory = "Dresses",
            description = "Flowing bohemian silhouette featuring intricate handmade embroidery on breathable rayon chiffon with tiered flare and adjustable waistband.",
            price = 58.0,
            originalPrice = 85.0,
            rating = 4.8f,
            reviewCount = 219,
            unit = "piece",
            images = listOf(
                "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600",
                "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=600"
            ),
            isFeatured = true,
            isNewArrival = true,
            tryOnCompatible = true,
            availableSizes = listOf("XS", "S", "M", "L", "XL"),
            availableColors = listOf("Emerald Green", "Terracotta Red", "Midnight Navy"),
            fabricOrIngredients = "Premium Viscose Rayon & Silk Thread Embroidery",
            tags = listOf("women", "dress", "summer", "party", "boho", "try-on")
        ),
        Product(
            id = "cl_3",
            name = "Heavyweight Oversized Streetwear T-Shirt",
            category = ProductCategory.CLOTHING,
            subCategory = "T-Shirts",
            description = "240 GSM drop-shoulder boxy fit tee with reinforced ribbed neckband and vintage mineral wash finish.",
            price = 28.0,
            originalPrice = 40.0,
            rating = 4.7f,
            reviewCount = 184,
            unit = "piece",
            images = listOf(
                "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600",
                "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=600"
            ),
            isDeal = true,
            tryOnCompatible = true,
            availableSizes = listOf("S", "M", "L", "XL", "XXL"),
            availableColors = listOf("Charcoal Black", "Sage Green", "Off-White"),
            fabricOrIngredients = "100% Combed Compact Cotton (240 GSM)",
            tags = listOf("unisex", "tshirt", "casual", "streetwear", "try-on")
        ),
        Product(
            id = "cl_4",
            name = "Flex-Stretch Slim Fit Raw Denim Jeans",
            category = ProductCategory.CLOTHING,
            subCategory = "Jeans",
            description = "Japanese selvedge-inspired stretch denim engineered for maximum mobility, deep indigo color fastness, and 5-pocket utility.",
            price = 52.0,
            originalPrice = 75.0,
            rating = 4.8f,
            reviewCount = 145,
            unit = "piece",
            images = listOf(
                "https://images.unsplash.com/photo-1542272604-780c96856592?w=600",
                "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=600"
            ),
            isBestSeller = true,
            tryOnCompatible = true,
            availableSizes = listOf("30", "32", "34", "36", "38"),
            availableColors = listOf("Deep Indigo", "Washed Slate", "Jet Black"),
            fabricOrIngredients = "98% Cotton, 2% Elastane Spandex",
            tags = listOf("men", "women", "jeans", "denim", "try-on")
        ),
        Product(
            id = "cl_5",
            name = "Kids Dinosaur Print Fleece Hoodie Set",
            category = ProductCategory.CLOTHING,
            subCategory = "Kids Wear",
            description = "Ultra-soft combed organic fleece hoodie paired with matching jogger pants. Hypoallergenic, skin-safe dyes.",
            price = 32.0,
            originalPrice = 45.0,
            rating = 4.9f,
            reviewCount = 92,
            unit = "set",
            images = listOf(
                "https://images.unsplash.com/photo-1519457431-44ccd64a579b?w=600"
            ),
            isNewArrival = true,
            tryOnCompatible = true,
            availableSizes = listOf("2-3Y", "4-5Y", "6-7Y", "8-9Y", "10-12Y"),
            availableColors = listOf("Mustard Dino", "Forest Camo"),
            fabricOrIngredients = "100% Organic Brushed Cotton Fleece",
            tags = listOf("kids", "hoodie", "winter", "cute", "try-on")
        ),
        Product(
            id = "cl_6",
            name = "CloudFoam Breathable Running Sneakers",
            category = ProductCategory.CLOTHING,
            subCategory = "Footwear",
            description = "Ultra-lightweight mesh knit running shoes with responsive foam cushioning and high-grip rubber outsole.",
            price = 69.0,
            originalPrice = 99.0,
            rating = 4.9f,
            reviewCount = 420,
            unit = "pair",
            images = listOf(
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600"
            ),
            isFeatured = true,
            isDeal = true,
            availableSizes = listOf("US 7", "US 8", "US 9", "US 10", "US 11", "US 12"),
            availableColors = listOf("Crimson Red", "Obsidian Black", "Triple White"),
            fabricOrIngredients = "Engineered FlyKnit Upper, EVA Midsole",
            tags = listOf("footwear", "shoes", "sneakers", "sports")
        ),
        Product(
            id = "cl_7",
            name = "Full-Grain Italian Leather Minimalist Belt & Wallet Set",
            category = ProductCategory.CLOTHING,
            subCategory = "Accessories",
            description = "Handcrafted genuine vegetable-tanned leather belt with matte gunmetal buckle, paired with an RFID-blocking slim wallet.",
            price = 39.0,
            originalPrice = 55.0,
            rating = 4.7f,
            reviewCount = 76,
            unit = "set",
            images = listOf(
                "https://images.unsplash.com/photo-1627123424574-724758594e93?w=600"
            ),
            isNewArrival = true,
            availableSizes = listOf("Standard (30-40 in)"),
            availableColors = listOf("Cognac Brown", "Midnight Black"),
            fabricOrIngredients = "100% Top-Grain Cowhide Leather",
            tags = listOf("accessories", "leather", "gift", "wallet")
        ),

        // ================= GROCERIES =================
        Product(
            id = "gr_1",
            name = "Fresh Organic Hass Avocados (Pack of 4)",
            category = ProductCategory.GROCERIES,
            subCategory = "Fruits",
            description = "Farm-fresh ripe and creamy Hass avocados, naturally tree-ripened and rich in heart-healthy monounsaturated fats.",
            price = 6.49,
            originalPrice = 8.99,
            rating = 4.9f,
            reviewCount = 540,
            unit = "pack of 4",
            images = listOf(
                "https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=600"
            ),
            isFeatured = true,
            isDeal = true,
            isBestSeller = true,
            fabricOrIngredients = "100% Organic Mexican Hass Avocados",
            tags = listOf("grocery", "fruit", "organic", "fresh", "healthy")
        ),
        Product(
            id = "gr_2",
            name = "Hydroponic Crisp Butterhead Lettuce & Baby Spinach",
            category = ProductCategory.GROCERIES,
            subCategory = "Vegetables",
            description = "Pesticide-free living greens harvested with roots intact for peak crispness, crunch, and nutritional retention.",
            price = 3.99,
            originalPrice = 5.20,
            rating = 4.8f,
            reviewCount = 230,
            unit = "250g box",
            images = listOf(
                "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600"
            ),
            isNewArrival = true,
            fabricOrIngredients = "Hydroponic Organic Greens",
            tags = listOf("grocery", "vegetable", "hydroponic", "salad")
        ),
        Product(
            id = "gr_3",
            name = "Grass-Fed Organic A2 Whole Milk & Artisanal Butter",
            category = ProductCategory.GROCERIES,
            subCategory = "Dairy Products",
            description = "Non-homogenized, low-temperature pasteurized whole milk from pasture-raised Jersey cows, rich in A2 beta-casein protein.",
            price = 5.79,
            originalPrice = 7.00,
            rating = 4.9f,
            reviewCount = 388,
            unit = "1 Gallon (3.8L)",
            images = listOf(
                "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=600"
            ),
            isBestSeller = true,
            fabricOrIngredients = "100% Grass-Fed Pasteurized Whole Milk",
            tags = listOf("grocery", "dairy", "milk", "organic", "fresh")
        ),
        Product(
            id = "gr_4",
            name = "Royal Aged Himalayan Long-Grain Basmati Rice",
            category = ProductCategory.GROCERIES,
            subCategory = "Rice & Grains",
            description = "Aged for 2 years in climate-controlled silos for exquisite nutty aroma, non-sticky delicate texture, and double elongation upon cooking.",
            price = 18.99,
            originalPrice = 24.99,
            rating = 4.9f,
            reviewCount = 610,
            unit = "5 kg bag",
            images = listOf(
                "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600"
            ),
            isDeal = true,
            isFeatured = true,
            fabricOrIngredients = "100% Traditional Himalayan Basmati Rice",
            tags = listOf("grocery", "grains", "rice", "staple")
        ),
        Product(
            id = "gr_5",
            name = "Cold-Pressed Extra Virgin Spanish Olive Oil",
            category = ProductCategory.GROCERIES,
            subCategory = "Cooking Essentials",
            description = "Single-estate Picual olive harvest first cold extraction. Acidity below 0.2% with notes of fresh green herbs and artichoke.",
            price = 14.50,
            originalPrice = 19.99,
            rating = 4.9f,
            reviewCount = 194,
            unit = "1 Litre bottle",
            images = listOf(
                "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600"
            ),
            isBestSeller = true,
            fabricOrIngredients = "100% Cold Pressed Extra Virgin Olive Oil",
            tags = listOf("grocery", "cooking", "oil", "healthy")
        ),
        Product(
            id = "gr_6",
            name = "Sparkling Artisanal Cold Brew & Organic Green Tea",
            category = ProductCategory.GROCERIES,
            subCategory = "Beverages",
            description = "18-hour cold steeped single-origin Ethiopian Yirgacheffe beans naturally sparkling with zero added sugars or preservatives.",
            price = 12.00,
            originalPrice = 15.00,
            rating = 4.7f,
            reviewCount = 89,
            unit = "Pack of 6 Cans",
            images = listOf(
                "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=600"
            ),
            isNewArrival = true,
            fabricOrIngredients = "Filtered Spring Water, Organic Single-Origin Arabica Coffee",
            tags = listOf("grocery", "beverage", "coffee", "sparkling")
        ),

        // ================= SEAFOOD =================
        Product(
            id = "sf_1",
            name = "Fresh Atlantic Wild Salmon Fillets (Sashimi Grade)",
            category = ProductCategory.SEAFOOD,
            subCategory = "Fresh Fish",
            description = "Line-caught wild Atlantic salmon with vivid orange marbling, high Omega-3 fatty acids, and buttery melt-in-the-mouth texture. Arrives chilled on fresh ice.",
            price = 24.99,
            originalPrice = 32.00,
            rating = 4.95f,
            reviewCount = 820,
            unit = "500g skin-on fillet",
            images = listOf(
                "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=600",
                "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=600"
            ),
            isDeal = true,
            isFeatured = true,
            isBestSeller = true,
            seafoodFreshness = SeafoodFreshnessInfo(
                catchTime = "Today 3:30 AM",
                origin = "North Atlantic Coastal Waters - Vessel #SeaKing 12",
                storageTemp = "Chilled at 0.5°C on crushed crystal ice",
                freshnessGrade = "Grade AAA+ Sashimi Certified",
                cleaningOption = "Deboned, descaled, vacuum sealed & iced"
            ),
            tags = listOf("seafood", "fish", "salmon", "fresh", "sashimi", "same-day")
        ),
        Product(
            id = "sf_2",
            name = "Jumbo King Tiger Prawns (Head-On / Deveined)",
            category = ProductCategory.SEAFOOD,
            subCategory = "Prawns",
            description = "Extra-large succulent tiger prawns boasting sweet, firm meat with characteristic crisp snap. Perfect for grilling, garlic butter saute, or paella.",
            price = 19.50,
            originalPrice = 26.00,
            rating = 4.9f,
            reviewCount = 475,
            unit = "500g (Approx 10-12 pcs)",
            images = listOf(
                "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=600"
            ),
            isFeatured = true,
            isBestSeller = true,
            seafoodFreshness = SeafoodFreshnessInfo(
                catchTime = "Today 4:15 AM",
                origin = "Bay Coast Marine Reefs",
                storageTemp = "Rapid-chilled at 0°C",
                freshnessGrade = "Export Quality Class 1",
                cleaningOption = "Cleaned, tail-on, deveined"
            ),
            tags = listOf("seafood", "prawns", "tiger prawns", "bbq", "fresh")
        ),
        Product(
            id = "sf_3",
            name = "Live Blue Swimmer Mud Crabs (Sweet & Meaty)",
            category = ProductCategory.SEAFOOD,
            subCategory = "Crabs",
            description = "Live premium mud crabs with dense, naturally sweet claw meat. Sourced directly from tidal mangrove bays under sustainable harvest.",
            price = 29.99,
            originalPrice = 38.00,
            rating = 4.85f,
            reviewCount = 310,
            unit = "1 kg (2-3 whole crabs)",
            images = listOf(
                "https://images.unsplash.com/photo-1559742811-82286364ceaf?w=600"
            ),
            isDeal = true,
            seafoodFreshness = SeafoodFreshnessInfo(
                catchTime = "Today 5:00 AM",
                origin = "Estuary Mangrove Reserves",
                storageTemp = "Live sea-water aerated container",
                freshnessGrade = "Live Catch Guaranteed",
                cleaningOption = "Whole Live or Cleaned & Cut into Quarters"
            ),
            tags = listOf("seafood", "crabs", "mud crab", "live", "curry")
        ),
        Product(
            id = "sf_4",
            name = "Whole Rock Lobster Tails (Cold Water)",
            category = ProductCategory.SEAFOOD,
            subCategory = "Lobsters",
            description = "Rich, decadent cold-water lobster tails with tender sweet white meat. Ideal for broiling with lemon herb butter sauce.",
            price = 42.00,
            originalPrice = 55.00,
            rating = 4.9f,
            reviewCount = 160,
            unit = "2 large tails (400g)",
            images = listOf(
                "https://images.unsplash.com/photo-1533745848184-3db07256e163?w=600"
            ),
            isFeatured = true,
            seafoodFreshness = SeafoodFreshnessInfo(
                catchTime = "Yesterday Night 11:30 PM",
                origin = "Deep Reef Oceanic Trench",
                storageTemp = "-1°C Chilled Storage",
                freshnessGrade = "Gourmet Chef Standard",
                cleaningOption = "Split & Butterflied on request"
            ),
            tags = listOf("seafood", "lobster", "luxury", "dinner")
        ),
        Product(
            id = "sf_5",
            name = "Tender Baby Squid / Calamari Rings (Ready to Cook)",
            category = ProductCategory.SEAFOOD,
            subCategory = "Squid",
            description = "Cleaned calamari tubes and rings with ink sacs and skin removed. Perfectly tender when flash fried or tossed in marinara.",
            price = 15.00,
            originalPrice = 20.00,
            rating = 4.7f,
            reviewCount = 198,
            unit = "500g cleaned pack",
            images = listOf(
                "https://images.unsplash.com/photo-1599084993091-1cb5c0721cc6?w=600"
            ),
            isNewArrival = true,
            seafoodFreshness = SeafoodFreshnessInfo(
                catchTime = "Today 4:45 AM",
                origin = "Deep Blue Coastal Drift",
                storageTemp = "0°C Ice Slurry",
                freshnessGrade = "Grade A",
                cleaningOption = "Fully cleaned and ring cut"
            ),
            tags = listOf("seafood", "squid", "calamari", "fried")
        ),
        Product(
            id = "sf_6",
            name = "Sun-Dried Traditional Anchovies & Mackerel (Zero Sand)",
            category = ProductCategory.SEAFOOD,
            subCategory = "Dry Fish",
            description = "Hygienically solar-tunnel dehydrated with natural sea salt. Completely sand-free and packed with umami flavor.",
            price = 9.99,
            originalPrice = 13.00,
            rating = 4.6f,
            reviewCount = 112,
            unit = "250g sealed pouch",
            images = listOf(
                "https://images.unsplash.com/photo-1534483509719-3feaee7c30da?w=600"
            ),
            fabricOrIngredients = "100% Coastal Anchovies & Pure Sea Salt",
            tags = listOf("seafood", "dry fish", "anchovy", "traditional")
        ),
        Product(
            id = "sf_7",
            name = "Chef's Seafood Marinade Paella & Grill Kit",
            category = ProductCategory.SEAFOOD,
            subCategory = "Ready-to-Cook Seafood",
            description = "Gourmet seafood mix containing pre-marinated salmon chunks, black tiger prawns, calamari, and mussels with Mediterranean rosemary garlic sauce.",
            price = 27.50,
            originalPrice = 35.00,
            rating = 4.95f,
            reviewCount = 280,
            unit = "750g meal kit",
            images = listOf(
                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600"
            ),
            isBestSeller = true,
            isDeal = true,
            seafoodFreshness = SeafoodFreshnessInfo(
                catchTime = "Today 5:30 AM",
                origin = "Fresh Coastal Catch Assortment",
                storageTemp = "1°C Marinated & Sealed",
                freshnessGrade = "Chef's Reserve",
                cleaningOption = "Pre-seasoned, cook in 12 mins"
            ),
            tags = listOf("seafood", "ready-to-cook", "paella", "dinner-kit")
        )
    )

    val sampleReviews = listOf(
        Review(
            id = "rev_1",
            productId = "sf_1",
            userName = "Marcus Chen",
            rating = 5.0f,
            date = "Yesterday",
            comment = "Unbelievable freshness! The salmon arrived iced solidly, smelled like fresh sea breeze, and made the best home sashimi I've ever had.",
            verifiedPurchase = true
        ),
        Review(
            id = "rev_2",
            productId = "cl_1",
            userName = "Elena Rostova",
            rating = 5.0f,
            date = "3 days ago",
            comment = "The AI Virtual Try-On accurately showed how it would fit my shoulders and chest! The fabric quality is immaculate.",
            verifiedPurchase = true,
            sizePurchased = "M"
        ),
        Review(
            id = "rev_3",
            productId = "gr_1",
            userName = "Sarah Jenkins",
            rating = 5.0f,
            date = "5 days ago",
            comment = "Every single avocado was in prime eating condition, completely green with zero bruises inside.",
            verifiedPurchase = true
        )
    )

    val sampleNotifications = listOf(
        NotificationItem(
            id = "notif_1",
            title = "Fresh Catch Morning Alert! 🐟",
            message = "Wild Atlantic Salmon & Live Mud Crabs just arrived at our cold-storage dock. Same-day delivery available!",
            timestamp = "10 mins ago",
            type = NotificationType.SPECIAL_OFFER
        ),
        NotificationItem(
            id = "notif_2",
            title = "Order #FM-98421 Out for Delivery 🚚",
            message = "Your FreshMart Express order is on the way! Estimated arrival in 25 mins.",
            timestamp = "1 hour ago",
            type = NotificationType.ORDER
        ),
        NotificationItem(
            id = "notif_3",
            title = "AI Virtual Try-On New Styles Added ✨",
            message = "Try the new summer dresses and oxford shirts on your photo instantly with AI fitting.",
            timestamp = "Yesterday",
            type = NotificationType.NEW_ARRIVAL
        ),
        NotificationItem(
            id = "notif_4",
            title = "Weekend Flash Sale ⚡",
            message = "Enjoy up to 40% OFF across Clothing and Groceries with code FRESHMART50.",
            timestamp = "2 days ago",
            type = NotificationType.FLASH_SALE
        )
    )

    val sampleOrders = listOf(
        Order(
            id = "ord_101",
            orderNumber = "FM-98421",
            items = listOf(
                CartItem(product = sampleProducts.first { it.id == "sf_1" }, quantity = 2),
                CartItem(product = sampleProducts.first { it.id == "gr_1" }, quantity = 1),
                CartItem(product = sampleProducts.first { it.id == "cl_1" }, quantity = 1, selectedSize = "L", selectedColor = "Sky Blue")
            ),
            subtotal = 101.47,
            discount = 15.00,
            deliveryFee = 0.0,
            tax = 6.92,
            total = 93.39,
            status = OrderStatus.OUT_FOR_DELIVERY,
            deliverySlot = sampleDeliverySlots[1],
            shippingAddress = sampleAddresses[0],
            paymentMethod = PaymentMethod.UPI,
            isSameDay = true,
            freshnessGuaranteed = true,
            createdAt = System.currentTimeMillis() - 7200000L,
            deliveryPersonName = "Rajesh Kumar (Express Courier)",
            deliveryPersonPhone = "+1 (555) 019-2834"
        ),
        Order(
            id = "ord_102",
            orderNumber = "FM-98110",
            items = listOf(
                CartItem(product = sampleProducts.first { it.id == "cl_2" }, quantity = 1, selectedSize = "M", selectedColor = "Emerald Green"),
                CartItem(product = sampleProducts.first { it.id == "sf_2" }, quantity = 1)
            ),
            subtotal = 77.50,
            discount = 10.00,
            deliveryFee = 0.0,
            tax = 5.40,
            total = 72.90,
            status = OrderStatus.DELIVERED,
            deliverySlot = sampleDeliverySlots[0],
            shippingAddress = sampleAddresses[0],
            paymentMethod = PaymentMethod.CREDIT_DEBIT_CARD,
            isSameDay = true,
            freshnessGuaranteed = true,
            createdAt = System.currentTimeMillis() - 86400000L * 2
        )
    )
}
