import { createRouter, createWebHashHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import ProductsView from '../views/ProductsView.vue'
import ProductDetailView from '../views/ProductDetailView.vue'
import CartView from '../views/CartView.vue'
import CheckoutView from '../views/CheckoutView.vue'
import OrdersView from '../views/OrdersView.vue'
import OrderDetailView from '../views/OrderDetailView.vue'

const routes = [
  { path: '/login', name: 'Login', component: LoginView },
  { path: '/', redirect: '/products' },
  { path: '/products', name: 'Products', component: ProductsView },
  { path: '/products/:id', name: 'ProductDetail', component: ProductDetailView },
  { path: '/cart', name: 'Cart', component: CartView },
  { path: '/checkout', name: 'Checkout', component: CheckoutView },
  { path: '/orders', name: 'Orders', component: OrdersView },
  { path: '/orders/:id', name: 'OrderDetail', component: OrderDetailView },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('bh_t')
  if (to.name !== 'Login' && !token) next({ name: 'Login' })
  else next()
})

export default router
